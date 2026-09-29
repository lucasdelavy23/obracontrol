package com.cursojava.ObraControl.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class RotasMvcTests {

    @Autowired private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldRedirectRootToHome() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void shouldRenderActivePages() throws Exception {
        assertPage("/home", "Home");
        assertPage("/cadastro-obra", "cadastro-obra");
        assertPage("/apartamento", "apartamento");
        assertPage("/checklist-apartamento", "checklist-apartamento");
        assertPage("/cadastrar-instalador", "cadastrar-instalador");
    }

    @Test
    void shouldServePageScripts() throws Exception {
        for (String script : new String[] {"obra.js", "apartamento.js", "checklist.js", "instalador.js"}) {
            mvc.perform(get("/" + script))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("text/javascript"));
        }
    }

    @Test
    void shouldNotExposeObsoletePages() throws Exception {
        for (String route : new String[] {
                "/obras", "/lista-obra", "/lista-prestador", "/login",
                "/cadastrar-prestador", "/form-obras", "/modelo_table"
        }) {
            mvc.perform(get(route)).andExpect(status().isNotFound());
        }
    }

    @Test
    void shouldNotShowInactiveUserMenu() throws Exception {
        mvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Configurações"))))
                .andExpect(content().string(not(containsString("Perfil"))))
                .andExpect(content().string(not(containsString("Sair"))));
    }

    private void assertPage(String route, String template) throws Exception {
        mvc.perform(get(route))
                .andExpect(status().isOk())
                .andExpect(view().name(template));
    }
}
