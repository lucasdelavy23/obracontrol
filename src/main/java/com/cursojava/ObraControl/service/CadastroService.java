package com.cursojava.ObraControl.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cursojava.ObraControl.dto.CidadeResumo;
import com.cursojava.ObraControl.dto.ConstrutoraResumo;
import com.cursojava.ObraControl.dto.EstadoResumo;
import com.cursojava.ObraControl.repository.CadastroRepository;

@Service
public class CadastroService {
    private final CadastroRepository cadastroRepository;

    public CadastroService(CadastroRepository cadastroRepository) {
        this.cadastroRepository = cadastroRepository;
    }

    public List<EstadoResumo> findStates() {
        return cadastroRepository.findStates();
    }

    public List<CidadeResumo> findCitiesByState(Long estadoId) {
        if (estadoId == null || estadoId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione um estado válido.");
        }
        if (!cadastroRepository.stateExists(estadoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado não encontrado.");
        }
        return cadastroRepository.findCitiesByState(estadoId);
    }

    public List<ConstrutoraResumo> findBuilders() {
        return cadastroRepository.findBuilders();
    }
}
