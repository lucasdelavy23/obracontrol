package com.cursojava.ObraControl.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class EdicaoCadastroTests {
    @Autowired private ObraRestController obraController;
    @Autowired private InstaladorRestController instaladorController;
    @Autowired private CadastroExceptionHandler exceptionHandler;
    @Autowired private JdbcTemplate jdbcTemplate;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(obraController, instaladorController)
                .setControllerAdvice(exceptionHandler).build();
        jdbcTemplate.update("INSERT INTO estado (id, nome, sigla) VALUES (950, 'Paraná', 'PR')");
        jdbcTemplate.update("INSERT INTO cidade (id, nome, estado_id) VALUES (950, 'Itapema', 950)");
        jdbcTemplate.update("INSERT INTO construtora (id, nome) VALUES (950, 'Outra construtora')");
    }

    @Test
    void shouldUpdateObraReferencesAndKeepIdStatusAndOtherRows() throws Exception {
        jdbcTemplate.update("UPDATE obra SET status = 'finalizada' WHERE id = 1");
        jdbcTemplate.update("INSERT INTO obra (id, nome, cidade_id, construtora_id) VALUES (950, 'Outra obra', 1, 1)");
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM obra", Long.class);
        mvc.perform(get("/api/obras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidadeId").value(1))
                .andExpect(jsonPath("$.estadoId").value(1))
                .andExpect(jsonPath("$.construtoraId").value(1));
        String body = """
                {"nome":"  Obra editada  ","cidadeId":950,"construtoraId":950,"endereco":"  Rua nova  "}
                """;
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/api/obras/1").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Obra editada"))
                    .andExpect(jsonPath("$.cidadeId").value(950))
                    .andExpect(jsonPath("$.estadoId").value(950))
                    .andExpect(jsonPath("$.construtoraId").value(950))
                    .andExpect(jsonPath("$.construtora").value("Outra construtora"));
        }
        mvc.perform(get("/api/obras/1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.endereco").value("Rua nova"));
        assertEquals(count, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM obra", Long.class));
        assertEquals("finalizada", jdbcTemplate.queryForObject("SELECT status FROM obra WHERE id = 1", String.class));
        assertEquals("Outra obra", jdbcTemplate.queryForObject("SELECT nome FROM obra WHERE id = 950", String.class));
    }

    @Test
    void shouldRejectInvalidObraEditsWithoutChangingStoredData() throws Exception {
        var before = jdbcTemplate.queryForMap("SELECT * FROM obra WHERE id = 1");
        for (String body : new String[] {
                "{\"nome\":\"\",\"cidadeId\":1,\"construtoraId\":1}",
                "{\"nome\":\"Alterada\",\"cidadeId\":99999,\"construtoraId\":1}",
                "{\"nome\":\"Alterada\",\"cidadeId\":1,\"construtoraId\":99999}",
                "{\"nome\":\"Alterada\",\"cidadeId\":1}",
                "{\"nome\":\"Alterada\",\"cidadeId\":1,\"construtoraId\":1,\"endereco\":\"" + "a".repeat(301) + "\"}"
        }) {
            mvc.perform(put("/api/obras/1").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").exists());
        }
        assertEquals(before, jdbcTemplate.queryForMap("SELECT * FROM obra WHERE id = 1"));
    }

    @Test
    void shouldUpdateInstallerAndClearOptionalPhoneWithoutCreatingRows() throws Exception {
        jdbcTemplate.update("INSERT INTO instalador (id, nome, telefone) VALUES (950, 'Outro instalador', '123')");
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instalador", Long.class);
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/api/instaladores/1").contentType(MediaType.APPLICATION_JSON).content("""
                    {"nome":"  Instalador editado  ","telefone":"  (48) 99999-1234  "}
                    """))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Instalador editado"))
                    .andExpect(jsonPath("$.telefone").value("(48) 99999-1234"));
        }
        mvc.perform(get("/api/instaladores/1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Instalador editado"));
        mvc.perform(put("/api/instaladores/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Instalador editado\",\"telefone\":\"  \"}"))
                .andExpect(status().isOk());
        assertEquals(1L, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instalador WHERE id = 1 AND telefone IS NULL", Long.class));
        assertEquals(count, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instalador", Long.class));
        assertEquals("Outro instalador", jdbcTemplate.queryForObject("SELECT nome FROM instalador WHERE id = 950", String.class));
    }

    @Test
    void shouldValidateInstallerCreationAndEditing() throws Exception {
        var before = jdbcTemplate.queryForMap("SELECT * FROM instalador WHERE id = 1");
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instalador", Long.class);
        for (String body : new String[] {
                "{}", "{\"nome\":\"  \"}",
                "{\"nome\":\"" + "a".repeat(201) + "\"}",
                "{\"nome\":\"Teste\",\"telefone\":\"" + "a".repeat(31) + "\"}", "{"
        }) {
            mvc.perform(put("/api/instaladores/1").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").exists());
            mvc.perform(post("/api/instaladores").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").exists());
        }
        assertEquals(before, jdbcTemplate.queryForMap("SELECT * FROM instalador WHERE id = 1"));
        assertEquals(count, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instalador", Long.class));
        mvc.perform(post("/api/instaladores").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Novo instalador\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber());
        mvc.perform(put("/api/instaladores/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"" + "a".repeat(200) + "\",\"telefone\":\"" + "1".repeat(30) + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundForMissingRecords() throws Exception {
        mvc.perform(get("/api/obras/99999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/instaladores/99999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/obras/99999").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Teste\",\"cidadeId\":1,\"construtoraId\":1}"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.mensagem").value("Obra não encontrada."));
        mvc.perform(put("/api/instaladores/99999").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Teste\"}"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.mensagem").value("Instalador não encontrado."));
    }
}
