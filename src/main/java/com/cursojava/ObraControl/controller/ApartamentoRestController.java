package com.cursojava.ObraControl.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cursojava.ObraControl.dto.CadastroApartamento;
import com.cursojava.ObraControl.model.Apartamento;
import com.cursojava.ObraControl.service.ApartamentoService;

@RestController
@RequestMapping("/api/apartamentos")
public class ApartamentoRestController {

    private final ApartamentoService apartamentoService;

    public ApartamentoRestController(ApartamentoService apartamentoService) {
        this.apartamentoService = apartamentoService;
    }

    @GetMapping
    public List<Apartamento> findByObra(@RequestParam Long obraId) {
        return apartamentoService.findByObra(obraId);
    }

    @GetMapping("/{id}")
    public Apartamento findById(@PathVariable Long id) {
        return apartamentoService.findById(id);
    }

    @PostMapping
    public Apartamento create(@RequestBody CadastroApartamento cadastro) {
        return apartamentoService.create(cadastro);
    }

    @PutMapping("/{id}")
    public Apartamento update(@PathVariable Long id, @RequestBody CadastroApartamento cadastro) {
        return apartamentoService.update(id, cadastro);
    }

    @DeleteMapping("/{id}")
    public Apartamento delete(@PathVariable Long id) {
        return apartamentoService.delete(id);
    }
}
