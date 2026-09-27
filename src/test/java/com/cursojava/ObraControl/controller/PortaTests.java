package com.cursojava.ObraControl.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class PortaTests {

    @Autowired private PortaRestController portaController;
    @Autowired private CadastroExceptionHandler exceptionHandler;
    @Autowired private JdbcTemplate jdbcTemplate;
    private MockMvc mvc;
    private long instaladorCarlos;
    private long instaladorAna;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(portaController)
                .setControllerAdvice(exceptionHandler).build();
        jdbcTemplate.update("INSERT INTO obra (id, nome, cidade_id, construtora_id) VALUES (900, 'Outra obra', 1, 1)");
        jdbcTemplate.update("INSERT INTO apartamento (id, numero, obra_id) VALUES (101, '101', 1)");
        jdbcTemplate.update("INSERT INTO apartamento (id, numero, obra_id) VALUES (102, '102', 1)");
        jdbcTemplate.update("INSERT INTO apartamento (id, numero, obra_id) VALUES (201, '201', 900)");
        jdbcTemplate.update("INSERT INTO instalador (id, nome, telefone) VALUES (2, 'Ana Souza', '(48) 99999-2002')");
        instaladorCarlos = jdbcTemplate.queryForObject(
                "SELECT MIN(id) FROM instalador WHERE nome = 'Carlos Eduardo Silva'", Long.class);
        instaladorAna = 2L;
    }

    @Test
    void shouldCreateListFindAndDeletePorta() throws Exception {
        for (String local : new String[] {"Sala de estar", "Cozinha"}) {
            mvc.perform(post("/api/portas").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"local\":\"  " + local + "  \",\"apartamentoId\":101}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.local").value(local))
                    .andExpect(jsonPath("$.apartamentoId").value(101))
                    .andExpect(jsonPath("$.instaladorId").doesNotExist())
                    .andExpect(jsonPath("$.etapas.montagem").value(false))
                    .andExpect(jsonPath("$.etapas.fixacao").value(false))
                    .andExpect(jsonPath("$.etapas.fechadura").value(false))
                    .andExpect(jsonPath("$.etapas.vistas").value(false))
                    .andExpect(jsonPath("$.etapas.acabamento").value(false));
        }
        mvc.perform(get("/api/portas").param("apartamentoId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].local").value("Cozinha"))
                .andExpect(jsonPath("$[1].local").value("Sala de estar"));
        mvc.perform(get("/api/portas").param("apartamentoId", "201"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        Long id = criar("Quarto", 101);
        mvc.perform(get("/api/portas/" + id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.local").value("Quarto"));
        mvc.perform(delete("/api/portas/" + id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        mvc.perform(get("/api/portas/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Porta não encontrada."));
    }

    @Test
    void shouldKeepEtapaKeysInExecutionOrder() throws Exception {
        String resposta = mvc.perform(post("/api/portas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"Entrada\",\"apartamentoId\":101}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etapas.length()").value(5))
                .andReturn().getResponse().getContentAsString();
        int anterior = -1;
        for (String etapa : new String[] {"montagem", "fixacao", "fechadura", "vistas", "acabamento"}) {
            int posicao = resposta.indexOf('"' + etapa + '"');
            assertTrue(posicao > anterior, "etapa fora de ordem: " + etapa);
            anterior = posicao;
        }
    }

    @Test
    void shouldUpdateLocalAndMovePortaToAnotherApartamento() throws Exception {
        Long id = criar("Sala", 101);
        assertEquals(1L, quantidadePortas(101));
        mvc.perform(put("/api/portas/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"  Sala ampliada  \",\"apartamentoId\":102}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.local").value("Sala ampliada"))
                .andExpect(jsonPath("$.apartamentoId").value(102));
        assertEquals(0L, quantidadePortas(101));
        assertEquals(1L, quantidadePortas(102));
    }

    @Test
    void shouldUpdateEtapaAndIgnoreUnknownStage() throws Exception {
        Long id = criar("Sala", 101);
        mvc.perform(put("/api/portas/" + id + "/etapas/fechadura").contentType(MediaType.APPLICATION_JSON)
                .content("true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etapas.fechadura").value(true))
                .andExpect(jsonPath("$.etapas.montagem").value(false));
        mvc.perform(put("/api/portas/" + id + "/etapas/MONTAGEM").contentType(MediaType.APPLICATION_JSON)
                .content("true"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.etapas.montagem").value(true));
        mvc.perform(put("/api/portas/" + id + "/etapas/fechadura").contentType(MediaType.APPLICATION_JSON)
                .content("false"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.etapas.fechadura").value(false));
        for (String etapa : new String[] {"espumar", "silicone", "porta; DROP TABLE porta", "MONTAGEM%20"}) {
            mvc.perform(put("/api/portas/" + id + "/etapas/" + etapa).contentType(MediaType.APPLICATION_JSON)
                    .content("true"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagem").value("Etapa do checklist inválida."));
        }
        mvc.perform(put("/api/portas/" + id + "/etapas/").contentType(MediaType.APPLICATION_JSON)
                .content("true"))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/portas/" + id + "/etapas/montagem").contentType(MediaType.APPLICATION_JSON)
                .content("\"sim\""))
                .andExpect(status().isBadRequest());
        assertEquals(1L, jdbcTemplate.queryForObject(
                "SELECT quantidade_portas FROM apartamento WHERE id = 101", Long.class));
    }

    @Test
    void shouldValidateFieldsAndReferencesWithoutWriting() throws Exception {
        for (String body : new String[] {
                "{}",
                "{\"local\":\"  \",\"apartamentoId\":101}",
                "{\"local\":\"" + "a".repeat(201) + "\",\"apartamentoId\":101}",
                "{\"local\":\"Sala\"}",
                "{\"local\":\"Sala\",\"apartamentoId\":99999}",
                "{\"local\":\"Sala\",\"apartamentoId\":0}",
                "{\"local\":\"Sala\",\"apartamentoId\":true}",
                "{"
        }) {
            mvc.perform(post("/api/portas").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").exists());
        }
        assertEquals(0L, countPortas());
        mvc.perform(get("/api/portas").param("apartamentoId", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Apartamento não encontrado."));
        mvc.perform(get("/api/portas").param("apartamentoId", "0"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/portas").param("apartamentoId", "abc"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/portas"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForMissingPortas() throws Exception {
        mvc.perform(get("/api/portas/99999"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.mensagem").value("Porta não encontrada."));
        mvc.perform(put("/api/portas/99999").contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"Sala\",\"apartamentoId\":101}"))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/portas/99999/etapas/montagem").contentType(MediaType.APPLICATION_JSON)
                .content("true"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/portas/99999")).andExpect(status().isNotFound());
    }

    @Test
    void shouldKeepQuantidadePortasAlignedWithPortas() throws Exception {
        assertEquals(0L, quantidadePortas(101));
        Long primeira = criar("Entrada", 101);
        criar("Sala", 101);
        assertEquals(2L, quantidadePortas(101));
        mvc.perform(delete("/api/portas/" + primeira))
                .andExpect(status().isOk());
        assertEquals(1L, quantidadePortas(101));
    }

    @Test
    void shouldLeaveInstaladorNuloWhenNotInformedAndDeletePortasWithApartamento() throws Exception {
        Long id = criar("Entrada", 101);
        assertNull(jdbcTemplate.queryForObject("SELECT instalador_id FROM porta WHERE id = ?", Long.class, id));
        assertEquals(1L, quantidadePortas(101));
        jdbcTemplate.update("DELETE FROM apartamento WHERE id = 101");
        assertEquals(0L, countPortas());
        assertEquals(0L, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM apartamento WHERE id = 101", Long.class));
    }

    @Test
    void shouldAssignChangeAndRemoveInstaller() throws Exception {
        String resposta = mvc.perform(post("/api/portas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":" + instaladorCarlos + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instaladorId").value((int) instaladorCarlos))
                .andReturn().getResponse().getContentAsString();
        Long id = Long.parseLong(resposta.replaceAll(".*\"id\":(\\d+).*", "$1"));
        assertEquals(Long.valueOf(instaladorCarlos), instaladorId(id));

        mvc.perform(put("/api/portas/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":" + instaladorAna + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instaladorId").value((int) instaladorAna))
                .andExpect(jsonPath("$.local").value("Sala"));
        assertEquals(Long.valueOf(instaladorAna), instaladorId(id));

        mvc.perform(put("/api/portas/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instaladorId").doesNotExist());
        assertNull(instaladorId(id));

        mvc.perform(get("/api/portas").param("apartamentoId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].instaladorId").doesNotExist());
    }

    @Test
    void shouldRejectInvalidInstallerWithoutWriting() throws Exception {
        for (String body : new String[] {
                "{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":0}",
                "{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":-1}",
                "{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":99999}",
                "{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":\"abc\"}"
        }) {
            mvc.perform(post("/api/portas").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        assertEquals(0L, countPortas());
        Long id = criar("Sala", 101);
        for (String body : new String[] {
                "{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":0}",
                "{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":99999}"
        }) {
            mvc.perform(put("/api/portas/" + id).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagem").value("Selecione um instalador válido."));
        }
        assertEquals("Sala", jdbcTemplate.queryForObject("SELECT local FROM porta WHERE id = ?", String.class, id));
        assertNull(instaladorId(id));
    }

    @Test
    void shouldKeepInstallerOnStageUpdate() throws Exception {
        Long id = criar("Sala", 101);
        mvc.perform(put("/api/portas/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":" + instaladorAna + "}"))
                .andExpect(status().isOk());
        mvc.perform(put("/api/portas/" + id + "/etapas/montagem").contentType(MediaType.APPLICATION_JSON)
                .content("true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etapas.montagem").value(true))
                .andExpect(jsonPath("$.instaladorId").value((int) instaladorAna));
        assertEquals(Long.valueOf(instaladorAna), instaladorId(id));
    }

    @Test
    void shouldClearInstallerWhenInstallerIsDeleted() throws Exception {
        Long id = criar("Sala", 101);
        mvc.perform(put("/api/portas/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"Sala\",\"apartamentoId\":101,\"instaladorId\":" + instaladorAna + "}"))
                .andExpect(status().isOk());
        jdbcTemplate.update("DELETE FROM instalador WHERE id = ?", instaladorAna);
        assertNull(instaladorId(id));
        mvc.perform(get("/api/portas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.local").value("Sala"))
                .andExpect(jsonPath("$.instaladorId").doesNotExist());
    }

    @Test
    void shouldNotChangeQuantidadePortasWhenAssigningInstaller() throws Exception {
        assertEquals(0L, quantidadePortas(101));
        Long id = criar("Entrada", 101);
        criar("Sala", 101);
        assertEquals(2L, quantidadePortas(101));
        mvc.perform(put("/api/portas/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"Entrada\",\"apartamentoId\":101,\"instaladorId\":" + instaladorAna + "}"))
                .andExpect(status().isOk());
        assertEquals(2L, quantidadePortas(101));
    }

    private Long instaladorId(long portaId) {
        return jdbcTemplate.queryForObject("SELECT instalador_id FROM porta WHERE id = ?", Long.class, portaId);
    }

    private Long criar(String local, long apartamentoId) throws Exception {
        String resposta = mvc.perform(post("/api/portas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"local\":\"" + local + "\",\"apartamentoId\":" + apartamentoId + "}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return Long.parseLong(resposta.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private Long quantidadePortas(long apartamentoId) {
        return jdbcTemplate.queryForObject(
                "SELECT quantidade_portas FROM apartamento WHERE id = ?", Long.class, apartamentoId);
    }

    private Long countPortas() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM porta", Long.class);
    }
}
