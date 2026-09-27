package com.cursojava.ObraControl.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursojava.ObraControl.model.Obra;
import com.cursojava.ObraControl.dto.CadastroObra;
import com.cursojava.ObraControl.service.ObraService;

@RestController
@RequestMapping("/api/obras")
public class ObraRestController {

    private final ObraService obraService;

    public ObraRestController(ObraService obraService) {
        this.obraService = obraService;
    }

    @GetMapping
    public List<Obra> listarTodas() {
        return obraService.listarTodas();
    }

    @GetMapping("/{id}")
    public Obra buscarPorId(@PathVariable Long id) {
        return obraService.buscarPorId(id);
    }

    @PostMapping
    public Obra create(@RequestBody CadastroObra obra) {
        return obraService.create(obra);
    }

    @DeleteMapping("/{id}")
    public Obra excluir(@PathVariable Long id) {
        return obraService.excluir(id);
    }

    @PutMapping("/{id}")
    public Obra update(@PathVariable Long id, @RequestBody CadastroObra obra) {
        return obraService.update(id, obra);
    }
}
