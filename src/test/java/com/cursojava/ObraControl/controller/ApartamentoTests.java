package com.cursojava.ObraControl.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class ApartamentoTests {
    @Autowired private ApartamentoRestController apartamentoController;
    @Autowired private CadastroExceptionHandler exceptionHandler;
    @Autowired private JdbcTemplate jdbcTemplate;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(apartamentoController)
                .setControllerAdvice(exceptionHandler).build();
        jdbcTemplate.update("INSERT INTO obra (id, nome, cidade_id, construtora_id) VALUES (900, 'Outra obra', 1, 1)");
    }

    @Test
    void shouldCreateListFindAndDeleteApartamento() throws Exception {
        for (String numero : new String[] {"101", "102"}) {
            mvc.perform(post("/api/apartamentos").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"numero\":\"  " + numero + "  \",\"obraId\":1}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.numero").value(numero))
                    .andExpect(jsonPath("$.obraId").value(1))
                    .andExpect(jsonPath("$.obra").value("Residencial Atlântico"))
                    .andExpect(jsonPath("$.quantidadePortas").value(0))
                    .andExpect(jsonPath("$.portas").doesNotExist());
        }
        mvc.perform(get("/api/apartamentos").param("obraId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].numero").value("101"))
                .andExpect(jsonPath("$[1].numero").value("102"));
        mvc.perform(get("/api/apartamentos").param("obraId", "900"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        Long id = criar("103", 1);
        mvc.perform(get("/api/apartamentos/" + id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.numero").value("103"));
        mvc.perform(delete("/api/apartamentos/" + id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        mvc.perform(get("/api/apartamentos/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Apartamento não encontrado."));
    }

    @Test
    void shouldUpdateNumberAndMoveToAnotherObraWithoutDuplicating() throws Exception {
        Long id = criar("101", 1);
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/api/apartamentos/" + id).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"numero\":\"  202  \",\"obraId\":900}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.numero").value("202"))
                    .andExpect(jsonPath("$.obraId").value(900))
                    .andExpect(jsonPath("$.obra").value("Outra obra"));
        }
        assertEquals(1L, countApartamentos());
        assertEquals("202", jdbcTemplate.queryForObject("SELECT numero FROM apartamento WHERE id = ?", String.class, id));
    }

    @Test
    void shouldRejectDuplicateNumberInSameObraButAllowInAnotherObra() throws Exception {
        criar("101", 1);
        mvc.perform(post("/api/apartamentos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"numero\":\"101\",\"obraId\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Já existe um apartamento com este número nesta obra."));
        mvc.perform(post("/api/apartamentos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"numero\":\"101\",\"obraId\":900}"))
                .andExpect(status().isOk());
        Long id = criar("102", 1);
        mvc.perform(put("/api/apartamentos/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"numero\":\"101\",\"obraId\":1}"))
                .andExpect(status().isConflict());
        mvc.perform(put("/api/apartamentos/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"numero\":\"102\",\"obraId\":1}"))
                .andExpect(status().isOk());
        assertEquals(3L, countApartamentos());
    }

    @Test
    void shouldValidateFieldsAndReferencesWithoutWriting() throws Exception {
        assertEquals(0L, countApartamentos());
        for (String body : new String[] {
                "{}",
                "{\"numero\":\"  \",\"obraId\":1}",
                "{\"numero\":\"" + "1".repeat(51) + "\",\"obraId\":1}",
                "{\"numero\":\"101\"}",
                "{\"numero\":\"101\",\"obraId\":99999}",
                "{\"numero\":\"101\",\"obraId\":0}",
                "{\"numero\":\"101\",\"obraId\":true}",
                "{"
        }) {
            mvc.perform(post("/api/apartamentos").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").exists());
        }
        assertEquals(0L, countApartamentos());
        mvc.perform(get("/api/apartamentos").param("obraId", "99999"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.mensagem").value("Obra não encontrada."));
        mvc.perform(get("/api/apartamentos").param("obraId", "0"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/apartamentos").param("obraId", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForMissingRecords() throws Exception {
        mvc.perform(get("/api/apartamentos/99999"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.mensagem").value("Apartamento não encontrado."));
        mvc.perform(put("/api/apartamentos/99999").contentType(MediaType.APPLICATION_JSON)
                .content("{\"numero\":\"101\",\"obraId\":1}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/apartamentos/99999")).andExpect(status().isNotFound());
    }

    @Test
    void shouldKeepQuantidadePortasAlignedWithPortas() throws Exception {
        Long id = criar("101", 1);
        jdbcTemplate.update("INSERT INTO porta (local, apartamento_id) VALUES ('Entrada', ?)", id);
        mvc.perform(put("/api/apartamentos/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"numero\":\"102\",\"obraId\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.quantidadePortas").value(1));
        assertEquals(1L, jdbcTemplate.queryForObject(
                "SELECT quantidade_portas FROM apartamento WHERE id = ?", Long.class, id));
        mvc.perform(delete("/api/apartamentos/" + id)).andExpect(status().isOk());
        assertEquals(0L, countApartamentos());
    }

    @Test
    void shouldDeletePortasWhenApartamentoIsRemoved() throws Exception {
        Long id = criar("101", 1);
        jdbcTemplate.update("INSERT INTO porta (local, apartamento_id, instalador_id) VALUES ('Entrada', ?, 1)", id);
        assertEquals(1L, countPortas());
        mvc.perform(delete("/api/apartamentos/" + id)).andExpect(status().isOk());
        assertEquals(0L, countPortas());
    }

    private Long criar(String numero, long obraId) throws Exception {
        String resposta = mvc.perform(post("/api/apartamentos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"numero\":\"" + numero + "\",\"obraId\":" + obraId + "}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return lerId(resposta);
    }

    private Long lerId(String json) {
        return Long.parseLong(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private Long countApartamentos() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM apartamento", Long.class);
    }

    private Long countPortas() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM porta", Long.class);
    }
}
