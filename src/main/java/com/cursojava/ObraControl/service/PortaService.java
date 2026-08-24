package com.cursojava.ObraControl.service;

import java.util.List;
import com.cursojava.ObraControl.repository.PortaRepository;
import org.springframework.stereotype.Service;
import com.cursojava.ObraControl.model.Porta;

@Service
public class PortaService {
    private final PortaRepository portaRepository;

    public PortaService(PortaRepository portaRepository) {
        this.portaRepository = portaRepository;
    }

    public List<Porta> listarTodas() {
        return portaRepository.listarTodas();
    }

    public Porta buscarporId(Long id) {
        return portaRepository.buscarPorId(id);
    }

    public Porta cadastrar(Porta porta) {
        porta.setId(null);
        return portaRepository.salvar(porta);
    }

    public Porta atualizarEtapa(Long id, String nomeEtapa, boolean concluida) {
        return portaRepository.atualizarEtapa(id, nomeEtapa, concluida);
    }

    public Porta excluir(Long id) {
        return portaRepository.excluir(id);
    }
}
