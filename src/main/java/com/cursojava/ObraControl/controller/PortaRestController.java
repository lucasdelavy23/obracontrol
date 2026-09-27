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

import com.cursojava.ObraControl.dto.CadastroPorta;
import com.cursojava.ObraControl.model.Porta;
import com.cursojava.ObraControl.service.PortaService;

@RestController
@RequestMapping("/api/portas")
public class PortaRestController {

    private final PortaService portaService;

    public PortaRestController(PortaService portaService) {
        this.portaService = portaService;
    }

    @GetMapping
    public List<Porta> findByApartamento(@RequestParam Long apartamentoId) {
        return portaService.findByApartamento(apartamentoId);
    }

    @GetMapping("/{id}")
    public Porta findById(@PathVariable Long id) {
        return portaService.findById(id);
    }

    @PostMapping
    public Porta create(@RequestBody CadastroPorta cadastro) {
        return portaService.create(cadastro);
    }

    @PutMapping("/{id}")
    public Porta update(@PathVariable Long id, @RequestBody CadastroPorta cadastro) {
        return portaService.update(id, cadastro);
    }

    @PutMapping("/{id}/etapas/{nomeEtapa}")
    public Porta updateStage(@PathVariable Long id, @PathVariable("nomeEtapa") String nomeEtapa,
            @RequestBody boolean concluida) {
        return portaService.updateStage(id, nomeEtapa, concluida);
    }

    @DeleteMapping("/{id}")
    public Porta delete(@PathVariable Long id) {
        return portaService.delete(id);
    }
}
