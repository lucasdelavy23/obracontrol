package com.cursojava.ObraControl.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cursojava.ObraControl.dto.CadastroPorta;
import com.cursojava.ObraControl.model.EtapaPorta;
import com.cursojava.ObraControl.model.Porta;
import com.cursojava.ObraControl.repository.ApartamentoRepository;
import com.cursojava.ObraControl.repository.PortaRepository;

@Service
public class PortaService {

    private final PortaRepository portaRepository;
    private final ApartamentoRepository apartamentoRepository;

    public PortaService(PortaRepository portaRepository, ApartamentoRepository apartamentoRepository) {
        this.portaRepository = portaRepository;
        this.apartamentoRepository = apartamentoRepository;
    }

    public List<Porta> findByApartamento(Long apartamentoId) {
        if (apartamentoId == null || apartamentoId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione um apartamento válido.");
        }
        if (apartamentoRepository.findById(apartamentoId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Apartamento não encontrado.");
        }
        return portaRepository.findByApartamento(apartamentoId);
    }

    public Porta findById(Long id) {
        Porta porta = portaRepository.findById(id);
        if (porta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Porta não encontrada.");
        }
        return porta;
    }

    @Transactional
    public Porta create(CadastroPorta cadastro) {
        Porta porta = portaRepository.save(validate(cadastro));
        atualizarQuantidadePortas(porta.getApartamentoId());
        return porta;
    }

    @Transactional
    public Porta update(Long id, CadastroPorta cadastro) {
        Porta existente = findById(id);
        Porta atualizada = portaRepository.update(id, validate(cadastro));
        atualizarQuantidadePortas(existente.getApartamentoId());
        if (!existente.getApartamentoId().equals(atualizada.getApartamentoId())) {
            atualizarQuantidadePortas(atualizada.getApartamentoId());
        }
        return atualizada;
    }

    @Transactional
    public Porta updateStage(Long id, String nomeEtapa, boolean concluida) {
        findById(id);
        EtapaPorta etapa = EtapaPorta.fromNome(nomeEtapa);
        if (etapa == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Etapa do checklist inválida.");
        }
        return portaRepository.updateStage(id, etapa, concluida);
    }

    @Transactional
    public Porta delete(Long id) {
        Porta existente = findById(id);
        portaRepository.delete(id);
        atualizarQuantidadePortas(existente.getApartamentoId());
        return existente;
    }

    private void atualizarQuantidadePortas(Long apartamentoId) {
        apartamentoRepository.atualizarQuantidadePortas(apartamentoId);
    }

    private CadastroPorta validate(CadastroPorta cadastro) {
        if (cadastro == null || cadastro.local() == null || cadastro.local().isBlank()
                || cadastro.local().strip().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe o local da porta com até 200 caracteres.");
        }
        if (cadastro.apartamentoId() == null || cadastro.apartamentoId() <= 0
                || apartamentoRepository.findById(cadastro.apartamentoId()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione um apartamento válido.");
        }
        return new CadastroPorta(cadastro.local().strip(), cadastro.apartamentoId());
    }
}
