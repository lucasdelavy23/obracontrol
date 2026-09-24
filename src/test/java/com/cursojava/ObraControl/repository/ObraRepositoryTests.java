package com.cursojava.ObraControl.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cursojava.ObraControl.model.Instalador;
import com.cursojava.ObraControl.model.Obra;

@SpringBootTest
class ObraRepositoryTests {

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private InstaladorRepository instaladorRepository;

    @Test
    void deveListarObrasComNomesResolvidos() {
        List<Obra> obras = obraRepository.listarTodas();

        assertFalse(obras.isEmpty());
        Obra obra = obras.get(0);
        assertNotNull(obra.getId());
        assertEquals("Residencial Atlântico", obra.getNome());
        assertEquals("Dallo", obra.getConstrutora());
        assertEquals("Itapema", obra.getCidade());
    }

    @Test
    void deveCadastrarOBuscarEExcluirObra() {
        Obra obra = new Obra(null, "Condomínio Jardim Europa", "Pascoalotto", "Joinville", "Avenida Brasil, 1500");
        Obra criada = obraRepository.salvar(obra);

        assertNotNull(criada.getId());
        assertEquals("Condomínio Jardim Europa", criada.getNome());
        assertEquals("Pascoalotto", criada.getConstrutora());
        assertEquals("Joinville", criada.getCidade());

        Obra encontrada = obraRepository.buscarPorId(criada.getId());
        assertNotNull(encontrada);
        assertEquals("Condomínio Jardim Europa", encontrada.getNome());

        Obra removida = obraRepository.excluir(criada.getId());
        assertNotNull(removida);
        assertNull(obraRepository.buscarPorId(criada.getId()));
    }

    @Test
    void deveCadastrarInstaladorComCidadeNova() {
        Obra obra = new Obra(null, "Residencial Parque Sul", "Procave", "Curitiba", "Rua XV de Novembro, 800");
        Obra criada = obraRepository.salvar(obra);

        assertNotNull(criada.getId());
        assertEquals("Curitiba", obraRepository.buscarPorId(criada.getId()).getCidade());
    }

    @Test
    void deveCadastrarInstaladorBuscarEExcluir() {
        Instalador instalador = new Instalador(null, "João Pedro Santos", "(48) 99999-1002");
        Instalador criado = instaladorRepository.salvar(instalador);

        assertNotNull(criado.getId());
        assertEquals("João Pedro Santos", criado.getNome());

        Instalador encontrado = instaladorRepository.buscarPorId(criado.getId());
        assertNotNull(encontrado);

        Instalador removido = instaladorRepository.excluir(criado.getId());
        assertNotNull(removido);
        assertNull(instaladorRepository.buscarPorId(criado.getId()));
    }

    @Test
    void deveBuscarInstaladoresExistentes() {
        List<Instalador> instaladores = instaladorRepository.listarTodas();
        assertTrue(instaladores.stream().anyMatch(i -> "Carlos Eduardo Silva".equals(i.getNome())));
    }
}