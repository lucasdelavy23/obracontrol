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
    public List<Porta> listarTodas() {
        return portaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Porta buscarPorId(@PathVariable Long id) {
        return portaService.buscarporId(id);
    }

    @PostMapping
    public Porta cadastrar(@RequestBody Porta porta) {
        return portaService.cadastrar(porta);
    }

    @PutMapping("/{id}/etapas/{nomeEtapa}")
    public Porta atualizarEtapa(@PathVariable Long id, @PathVariable("nomeEtapa") String nomeEtapa,
            @RequestBody boolean concluida) {
        return portaService.atualizarEtapa(id, nomeEtapa, concluida);
    }

    @DeleteMapping("/{id}")
    public Porta excluir(@PathVariable Long id) {
        return portaService.excluir(id);
    }
}
