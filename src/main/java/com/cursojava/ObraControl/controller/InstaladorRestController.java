package com.cursojava.ObraControl.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursojava.ObraControl.model.Instalador;
import com.cursojava.ObraControl.service.InstaladorService;

@RestController
@RequestMapping("/api/instaladores")
public class InstaladorRestController {

    private final InstaladorService instaladorService;

    public InstaladorRestController(InstaladorService instaladorService) {
        this.instaladorService = instaladorService;
    }

    @GetMapping
    public List<Instalador> listarTodas() {
        return instaladorService.listarTodas();
    }

    @GetMapping("/{id}")
    public Instalador buscarPorId(@PathVariable Long id) {
        return instaladorService.buscarPorId(id);
    }

    @PostMapping
    public Instalador cadastrar(@RequestBody Instalador instalador) {
        return instaladorService.cadastrar(instalador);
    }

    @DeleteMapping("/{id}")
    public Instalador excluir(@PathVariable Long id) {
        return instaladorService.excluir(id);
    }
}