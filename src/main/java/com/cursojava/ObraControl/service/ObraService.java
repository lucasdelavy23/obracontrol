package com.cursojava.ObraControl.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cursojava.ObraControl.dto.CadastroObra;
import com.cursojava.ObraControl.dto.AtualizacaoStatusObra;
import com.cursojava.ObraControl.model.Obra;
import com.cursojava.ObraControl.model.StatusObra;
import com.cursojava.ObraControl.repository.CadastroRepository;
import com.cursojava.ObraControl.repository.ObraRepository;

@Service
public class ObraService {
    private final ObraRepository obraRepository;
    private final CadastroRepository cadastroRepository;

    public ObraService(ObraRepository obraRepository, CadastroRepository cadastroRepository) {
        this.obraRepository = obraRepository;
        this.cadastroRepository = cadastroRepository;
    }

    public List<Obra> listarTodas() {
        return obraRepository.listarTodas();
    }

    public Obra buscarPorId(Long id) {
        Obra obra = obraRepository.buscarPorId(id);
        if (obra == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra não encontrada.");
        }
        return obra;
    }

    @Transactional
    public Obra create(CadastroObra obra) {
        return obraRepository.save(validate(obra));
    }

    @Transactional
    public Obra update(Long id, CadastroObra obra) {
        buscarPorId(id);
        Obra updated = obraRepository.update(id, validate(obra));
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra não encontrada.");
        }
        return updated;
    }

    @Transactional
    public Obra updateStatus(Long id, AtualizacaoStatusObra atualizacao) {
        buscarPorId(id);
        StatusObra status = StatusObra.fromNome(atualizacao == null ? null : atualizacao.status());
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status da obra inválido.");
        }
        return obraRepository.updateStatus(id, status);
    }

    private CadastroObra validate(CadastroObra obra) {
        if (obra.nome() == null || obra.nome().isBlank() || obra.nome().strip().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o nome da obra com até 200 caracteres.");
        }
        if (obra.endereco() != null && obra.endereco().strip().length() > 300) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O endereço deve ter até 300 caracteres.");
        }
        if (obra.cidadeId() == null || obra.cidadeId() <= 0 || !cadastroRepository.cityExists(obra.cidadeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione uma cidade válida.");
        }
        if (obra.construtoraId() == null || obra.construtoraId() <= 0
                || !cadastroRepository.builderExists(obra.construtoraId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione uma construtora válida.");
        }
        return new CadastroObra(obra.nome().strip(), obra.cidadeId(), obra.construtoraId(),
                obra.endereco() == null || obra.endereco().isBlank() ? null : obra.endereco().strip());
    }

    public Obra excluir(Long id) {
        return obraRepository.excluir(id);
    }
}
