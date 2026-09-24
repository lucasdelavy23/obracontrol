package com.cursojava.ObraControl.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cursojava.ObraControl.model.Obra;
import com.cursojava.ObraControl.repository.ObraRepository;

@Service
public class ObraService {
    private final ObraRepository obraRepository;

    public ObraService(ObraRepository obraRepository) {
        this.obraRepository = obraRepository;
    }

    public List<Obra> listarTodas() {
        return obraRepository.listarTodas();
    }

    public Obra buscarPorId(Long id) {
        return obraRepository.buscarPorId(id);
    }

    public Obra cadastrar(Obra obra) {
        obra.setId(null);
        return obraRepository.salvar(obra);
    }

    public Obra excluir(Long id) {
        return obraRepository.excluir(id);
    }
}