package com.cursojava.ObraControl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class ObrasController {

    @GetMapping("/obras")
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
        return "Home";
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
