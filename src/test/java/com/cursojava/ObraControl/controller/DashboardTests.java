package com.cursojava.ObraControl.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.cursojava.ObraControl.dto.Dashboard;
import com.cursojava.ObraControl.dto.ProgressoObra;
import com.cursojava.ObraControl.service.DashboardService;

@SpringBootTest
@Transactional
class DashboardTests {

    @Autowired private DashboardService dashboardService;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        jdbcTemplate.update("INSERT INTO obra (id, nome, cidade_id, construtora_id, status) "
                + "VALUES (900, 'Obra Finalizada', 1, 1, 'finalizada')");
        jdbcTemplate.update("INSERT INTO obra (id, nome, cidade_id, construtora_id, status) "
                + "VALUES (901, 'Obra Vazia', 1, 1, 'aberta')");
        jdbcTemplate.update("INSERT INTO apartamento (id, numero, obra_id) VALUES (101, '101', 1)");
        jdbcTemplate.update("INSERT INTO apartamento (id, numero, obra_id) VALUES (201, '201', 900)");
        jdbcTemplate.update("INSERT INTO instalador (id, nome) VALUES (900, 'Sem Portas')");
        jdbcTemplate.update("""
                INSERT INTO porta (id, local, apartamento_id, instalador_id,
                                   montagem, fixacao, fechadura, vistas, acabamento)
                VALUES (1001, 'Entrada', 101, 1, TRUE, TRUE, TRUE, TRUE, TRUE),
                       (1002, 'Sala', 101, NULL, TRUE, TRUE, FALSE, FALSE, FALSE),
                       (1003, 'Quarto', 201, NULL, FALSE, FALSE, FALSE, FALSE, FALSE)
                """);
    }

    @Test
    void shouldCalculateSummaryAndProgressIncludingEmptyRecords() {
        Dashboard dashboard = dashboardService.getDashboard();

        assertEquals(3, dashboard.resumo().totalObras());
        assertEquals(2, dashboard.resumo().obrasAbertas());
        assertEquals(1, dashboard.resumo().obrasFinalizadas());
        assertEquals(2, dashboard.resumo().totalApartamentos());
        assertEquals(3, dashboard.resumo().totalPortas());
        assertEquals(1, dashboard.resumo().portasConcluidas());
        assertEquals(2, dashboard.resumo().portasSemInstalador());
        assertEquals(2, dashboard.resumo().totalInstaladores());

        ProgressoObra residencial = buscarObra(dashboard, "Residencial Atlântico");
        assertEquals(1, residencial.quantidadeApartamentos());
        assertEquals(2, residencial.quantidadePortas());
        assertEquals(7, residencial.etapasConcluidas());
        assertEquals(70, residencial.getPercentual());

        ProgressoObra vazia = buscarObra(dashboard, "Obra Vazia");
        assertEquals(0, vazia.quantidadeApartamentos());
        assertEquals(0, vazia.quantidadePortas());
        assertEquals(0, vazia.getPercentual());

        assertEquals("Carlos Eduardo Silva", dashboard.cargaInstaladores().get(0).nome());
        assertEquals(1, dashboard.cargaInstaladores().get(0).quantidadePortas());
        assertTrue(dashboard.cargaInstaladores().stream()
                .anyMatch(item -> item.nome().equals("Sem Portas") && item.quantidadePortas() == 0));
    }

    @Test
    void shouldRenderHomeWithDatabaseValues() throws Exception {
        String html = mvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("Home"))
                .andExpect(model().attributeExists("dashboard"))
                .andReturn().getResponse().getContentAsString();

        assertHtmlValue(html, "total-obras", "3");
        assertHtmlValue(html, "total-apartamentos", "2");
        assertHtmlValue(html, "total-portas", "3");
        assertHtmlValue(html, "total-instaladores", "2");
        assertHtmlValue(html, "obras-abertas", "2");
        assertHtmlValue(html, "obras-finalizadas", "1");
        assertHtmlValue(html, "portas-concluidas", "1");
        assertHtmlValue(html, "portas-sem-instalador", "2");
        assertFalse(html.contains("Progresso geral das instalações"));
    }

    @Test
    void shouldReturnZerosWhenDatabaseIsEmpty() {
        jdbcTemplate.update("DELETE FROM porta");
        jdbcTemplate.update("DELETE FROM apartamento");
        jdbcTemplate.update("DELETE FROM obra");
        jdbcTemplate.update("DELETE FROM instalador");

        Dashboard dashboard = dashboardService.getDashboard();

        assertEquals(0, dashboard.resumo().totalObras());
        assertEquals(0, dashboard.resumo().totalApartamentos());
        assertEquals(0, dashboard.resumo().totalPortas());
        assertEquals(0, dashboard.resumo().totalInstaladores());
        assertTrue(dashboard.progressoObras().isEmpty());
        assertTrue(dashboard.cargaInstaladores().isEmpty());
    }

    private ProgressoObra buscarObra(Dashboard dashboard, String nome) {
        return dashboard.progressoObras().stream()
                .filter(obra -> obra.nome().equals(nome))
                .findFirst()
                .orElseThrow();
    }

    private void assertHtmlValue(String html, String id, String value) {
        assertTrue(html.matches("(?s).*id=\"" + id + "\"[^>]*>\\s*" + value + "\\s*</[^>]+>.*"),
                "Valor não renderizado para #" + id);
    }
}
