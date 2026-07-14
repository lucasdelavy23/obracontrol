package com.cursojava.ObraControl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ObrasController {

    @PostMapping("/obras")

    public String obras() {
        return "obras";
    }

    @GetMapping("/apartamento")
    public String apartamento() {
        return "apartamento";
    }

    @GetMapping("/cadastrar-instalador")
    public String cadastrar_instalador() {
        return "cadastrar-instalador";
    }

    @GetMapping("/cadastro-obra")
    public String cadastro_obra() {
        return "cadastro-obra";
    }

    @GetMapping("/checklist-apartamento")
    public String checklist_apartamento() {
        return "checklist-apartamento";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/lista-obra")
    public String lita_obra() {
        return "lista-obra";
    }

    @GetMapping("/lista-prestador")
    public String lita_prestador() {
        return "lista-prestador";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
