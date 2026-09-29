package com.cursojava.ObraControl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginasController {

    @GetMapping("/apartamento")
    public String apartments() {
        return "apartamento";
    }

    @GetMapping("/cadastrar-instalador")
    public String installerRegistration() {
        return "cadastrar-instalador";
    }

    @GetMapping("/cadastro-obra")
    public String workRegistration() {
        return "cadastro-obra";
    }

    @GetMapping("/checklist-apartamento")
    public String apartmentChecklist(Model model) {
        model.addAttribute("cacheBust", System.currentTimeMillis());
        return "checklist-apartamento";
    }
}
