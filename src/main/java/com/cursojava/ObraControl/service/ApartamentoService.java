package com.cursojava.ObraControl.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cursojava.ObraControl.dto.CadastroApartamento;
import com.cursojava.ObraControl.model.Apartamento;
import com.cursojava.ObraControl.repository.ApartamentoRepository;
import com.cursojava.ObraControl.repository.CadastroRepository;

@Service
public class ApartamentoService {

    private final ApartamentoRepository apartamentoRepository;
    private final CadastroRepository cadastroRepository;

    public ApartamentoService(ApartamentoRepository apartamentoRepository, CadastroRepository cadastroRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.cadastroRepository = cadastroRepository;
    }

    public List<Apartamento> findByObra(Long obraId) {
        if (obraId == null || obraId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione uma obra válida.");
        }
        if (!cadastroRepository.obraExists(obraId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra não encontrada.");
        }
        return apartamentoRepository.findByObra(obraId);
    }

    public Apartamento findById(Long id) {
        Apartamento apartamento = apartamentoRepository.findById(id);
        if (apartamento == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Apartamento não encontrado.");
        }
        return apartamento;
    }

    @Transactional
    public Apartamento create(CadastroApartamento cadastro) {
        return apartamentoRepository.save(validate(cadastro, null));
    }

    @Transactional
    public Apartamento update(Long id, CadastroApartamento cadastro) {
        findById(id);
        Apartamento atualizado = apartamentoRepository.update(id, validate(cadastro, id));
        if (atualizado == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Apartamento não encontrado.");
        }
        return atualizado;
    }

    @Transactional
    public Apartamento delete(Long id) {
        Apartamento existente = findById(id);
        apartamentoRepository.delete(id);
        return existente;
    }

    private CadastroApartamento validate(CadastroApartamento cadastro, Long excludeId) {
        if (cadastro == null || cadastro.numero() == null || cadastro.numero().isBlank()
                || cadastro.numero().strip().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe o número do apartamento com até 50 caracteres.");
        }
        if (cadastro.obraId() == null || cadastro.obraId() <= 0 || !cadastroRepository.obraExists(cadastro.obraId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione uma obra válida.");
        }
        String numero = cadastro.numero().strip();
        if (apartamentoRepository.existsByNumero(cadastro.obraId(), numero, excludeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um apartamento com este número nesta obra.");
        }
        return new CadastroApartamento(numero, cadastro.obraId());
    }
}
