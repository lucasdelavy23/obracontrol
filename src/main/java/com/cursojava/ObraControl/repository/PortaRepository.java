package com.cursojava.ObraControl.repository;

import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;

import com.cursojava.ObraControl.model.Porta;

@Repository
public class PortaRepository {
    private List<Porta> portas = new ArrayList<>();
    private Long proximoId = 1L;

    public Porta salvar(Porta porta) {
        porta.setId(proximoId);
        proximoId++;

        portas.add(porta);

        return porta;
    }

    public PortaRepository() {
        salvar(new Porta(null, "Porta 1"));
        salvar(new Porta(null, "Porta 2"));
    }

    public List<Porta> listarTodas() {
        return portas;
    }

    public Porta buscarPorId(Long id) {
        for (Porta porta : portas) {
            if (porta.getId().equals(id)) {
                return porta;
            }
        }
        return null;
    }

    public Porta atualizarEtapa(Long id, String nomeEtapa, Boolean concluida) {

        Porta portaExistente = buscarPorId(id);

        if (portaExistente == null) {
            return null;
        }

        if (!portaExistente.getEtapas().containsKey(nomeEtapa)) {
            return null;
        }

        portaExistente.getEtapas().put(nomeEtapa, concluida);

        return portaExistente;
    }

    public Porta excluir(Long id) {
        Porta portaExistente = buscarPorId(id);
        if (portaExistente == null) {
            return null;
        }

        portas.remove(portaExistente);
        return portaExistente;
    }
}