package com.cursojava.ObraControl.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursojava.ObraControl.dto.CidadeResumo;
import com.cursojava.ObraControl.dto.ConstrutoraResumo;
import com.cursojava.ObraControl.dto.EstadoResumo;
import com.cursojava.ObraControl.service.CadastroService;

@RestController
@RequestMapping("/api")
public class CadastroRestController {
    private final CadastroService cadastroService;

    public CadastroRestController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @GetMapping("/estados")
    public List<EstadoResumo> findStates() {
        return cadastroService.findStates();
    }

    @GetMapping("/estados/{estadoId}/cidades")
    public List<CidadeResumo> findCitiesByState(@PathVariable Long estadoId) {
        return cadastroService.findCitiesByState(estadoId);
    }

    @GetMapping("/construtoras")
    public List<ConstrutoraResumo> findBuilders() {
        return cadastroService.findBuilders();
    }
}
