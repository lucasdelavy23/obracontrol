package com.cursojava.ObraControl.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cursojava.ObraControl.dto.CadastroInstalador;
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
        Instalador instalador = instaladorRepository.buscarPorId(id);
        if (instalador == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instalador não encontrado.");
        }
        return instalador;
    }

    @Transactional
    public Instalador create(CadastroInstalador instalador) {
        return instaladorRepository.salvar(validate(instalador));
    }

    @Transactional
    public Instalador update(Long id, CadastroInstalador instalador) {
        buscarPorId(id);
        Instalador updated = instaladorRepository.update(id, validate(instalador));
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instalador não encontrado.");
        }
        return updated;
    }

    private Instalador validate(CadastroInstalador instalador) {
        if (instalador.nome() == null || instalador.nome().isBlank() || instalador.nome().strip().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o nome do instalador com até 200 caracteres.");
        }
        if (instalador.telefone() != null && instalador.telefone().strip().length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O telefone deve ter até 30 caracteres.");
        }
        return new Instalador(null, instalador.nome().strip(),
                instalador.telefone() == null || instalador.telefone().isBlank() ? null : instalador.telefone().strip());
    }

    public Instalador excluir(Long id) {
        return instaladorRepository.excluir(id);
    }
}
