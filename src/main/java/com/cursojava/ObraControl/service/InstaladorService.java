package com.cursojava.ObraControl.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cursojava.ObraControl.model.Instalador;
import com.cursojava.ObraControl.repository.InstaladorRepository;

@Service
public class InstaladorService {
    private final InstaladorRepository instaladorRepository;

    public InstaladorService(InstaladorRepository instaladorRepository) {
        this.instaladorRepository = instaladorRepository;
    }

    public List<Instalador> listarTodas() {
        return instaladorRepository.listarTodas();
    }

    public Instalador buscarPorId(Long id) {
        return instaladorRepository.buscarPorId(id);
    }

    public Instalador cadastrar(Instalador instalador) {
        instalador.setId(null);
        return instaladorRepository.salvar(instalador);
    }

    public Instalador excluir(Long id) {
        return instaladorRepository.excluir(id);
    }
}