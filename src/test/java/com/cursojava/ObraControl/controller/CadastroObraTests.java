package com.cursojava.ObraControl.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CadastroObraTests {
    @Autowired
    private ObraRestController obraController;

    @Autowired
    private CadastroRestController cadastroController;

    @Autowired
    private CadastroExceptionHandler exceptionHandler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(obraController, cadastroController)
                .setControllerAdvice(exceptionHandler).build();
        jdbcTemplate.update("INSERT INTO estado (id, nome, sigla) VALUES (900, 'Paraná', 'PR')");
        jdbcTemplate.update("INSERT INTO estado (id, nome, sigla) VALUES (901, 'São Paulo', 'SP')");
        jdbcTemplate.update("INSERT INTO cidade (id, nome, estado_id) VALUES (900, 'Cidade homônima', 1)");
        jdbcTemplate.update("INSERT INTO cidade (id, nome, estado_id) VALUES (901, 'Cidade homônima', 900)");
        jdbcTemplate.update("INSERT INTO construtora (id, nome) VALUES (900, 'Construtora do banco')");
    }

    @Test
    void shouldLoadCatalogsAndFilterCitiesByState() throws Exception {
        mvc.perform(get("/api/estados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 900)].sigla").value("PR"));
        mvc.perform(get("/api/construtoras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 900)].nome").value("Construtora do banco"));
        mvc.perform(get("/api/estados/900/cidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(901))
                .andExpect(jsonPath("$[0].estadoId").value(900));
        mvc.perform(get("/api/estados/901/cidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/estados/99999/cidades"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Estado não encontrado."));
        mvc.perform(get("/api/estados/invalido/cidades"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void shouldPersistSelectedIdsAndReturnNamesWithoutCreatingCatalogs() throws Exception {
        Long citiesBefore = count("cidade");
        Long buildersBefore = count("construtora");
        mvc.perform(post("/api/obras").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome":"  Obra por IDs  ","cidadeId":901,"construtoraId":900,"endereco":"  Rua A  "}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Obra por IDs"))
                .andExpect(jsonPath("$.cidade").value("Cidade homônima"))
                .andExpect(jsonPath("$.construtora").value("Construtora do banco"))
                .andExpect(jsonPath("$.endereco").value("Rua A"))
                .andExpect(jsonPath("$.status").value("aberta"))
                .andExpect(jsonPath("$.apartamentos").doesNotExist());

        Long id = jdbcTemplate.queryForObject("SELECT id FROM obra WHERE nome = 'Obra por IDs'", Long.class);
        assertEquals(901L, jdbcTemplate.queryForObject("SELECT cidade_id FROM obra WHERE id = ?", Long.class, id));
        assertEquals(900L, jdbcTemplate.queryForObject("SELECT construtora_id FROM obra WHERE id = ?", Long.class, id));
        assertEquals(citiesBefore, count("cidade"));
        assertEquals(buildersBefore, count("construtora"));
        mvc.perform(get("/api/obras/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidade").value("Cidade homônima"))
                .andExpect(jsonPath("$.construtora").value("Construtora do banco"));
        mvc.perform(get("/api/obras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nome == 'Obra por IDs')].construtora").value("Construtora do banco"));
    }

    @Test
    void shouldRejectMissingOrUnknownReferencesWithoutWrites() throws Exception {
        Long obrasBefore = count("obra");
        Long citiesBefore = count("cidade");
        Long buildersBefore = count("construtora");
        for (String body : new String[] {
                "{\"nome\":\"Teste\",\"cidadeId\":99999,\"construtoraId\":1}",
                "{\"nome\":\"Teste\",\"cidadeId\":1,\"construtoraId\":99999}",
                "{\"nome\":\"Teste\",\"construtoraId\":1}",
                "{\"nome\":\"Teste\",\"cidadeId\":1}",
                "{\"nome\":\"Teste\",\"cidadeId\":0,\"construtoraId\":1}",
                "{\"nome\":\"Teste\",\"cidade\":\"Cidade nova\",\"construtora\":\"Construtora nova\"}"
        }) {
            mvc.perform(post("/api/obras").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagem").exists());
        }
        assertEquals(obrasBefore, count("obra"));
        assertEquals(citiesBefore, count("cidade"));
        assertEquals(buildersBefore, count("construtora"));
    }

    @Test
    void shouldValidateNameAddressAndMalformedInput() throws Exception {
        Long obrasBefore = count("obra");
        for (String body : new String[] {
                "{\"nome\":\"  \",\"cidadeId\":1,\"construtoraId\":1}",
                "{\"cidadeId\":1,\"construtoraId\":1}",
                "{\"nome\":\"" + "a".repeat(201) + "\",\"cidadeId\":1,\"construtoraId\":1}",
                "{\"nome\":\"Teste\",\"cidadeId\":1,\"construtoraId\":1,\"endereco\":\"" + "a".repeat(301) + "\"}",
                "{\"nome\":\"Teste\",\"cidadeId\":\"abc\",\"construtoraId\":1}",
                "{"
        }) {
            mvc.perform(post("/api/obras").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagem").exists());
        }
        assertEquals(obrasBefore, count("obra"));
    }

    @Test
    void shouldAcceptSchemaLimitsAndOptionalAddress() throws Exception {
        mvc.perform(post("/api/obras").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"" + "a".repeat(200) + "\",\"cidadeId\":1,\"construtoraId\":1,\"endereco\":\""
                        + "a".repeat(300) + "\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/obras").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Sem endereço\",\"cidadeId\":1,\"construtoraId\":1}"))
                .andExpect(status().isOk());
        assertEquals(1L, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM obra WHERE nome = 'Sem endereço' AND endereco IS NULL", Long.class));
    }

    private Long count(String table) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
    }
}
