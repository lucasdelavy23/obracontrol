package com.cursojava.ObraControl.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cursojava.ObraControl.dto.CadastroPorta;
import com.cursojava.ObraControl.model.EtapaPorta;
import com.cursojava.ObraControl.model.Porta;

@Repository
public class PortaRepository {

    private final JdbcTemplate jdbcTemplate;

    public PortaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_BASE = """
            SELECT id, local, apartamento_id, instalador_id,
                   montagem, fixacao, fechadura, vistas, acabamento
            FROM porta
            """;

    public List<Porta> findByApartamento(Long apartamentoId) {
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE apartamento_id = ? ORDER BY local, id",
                this::mapPorta, apartamentoId);
    }

    public Porta findById(Long id) {
        List<Porta> portas = jdbcTemplate.query(SELECT_BASE + " WHERE id = ?", this::mapPorta, id);
        return portas.isEmpty() ? null : portas.get(0);
    }

    public Porta save(CadastroPorta cadastro) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO porta (local, apartamento_id, instalador_id) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cadastro.local());
            ps.setLong(2, cadastro.apartamentoId());
            definirInstalador(ps, 3, cadastro.instaladorId());
            return ps;
        }, keyHolder);

        return findById(keyHolder.getKey().longValue());
    }

    public Porta update(Long id, CadastroPorta cadastro) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE porta SET local = ?, apartamento_id = ?, instalador_id = ? WHERE id = ?");
            ps.setString(1, cadastro.local());
            ps.setLong(2, cadastro.apartamentoId());
            definirInstalador(ps, 3, cadastro.instaladorId());
            ps.setLong(4, id);
            return ps;
        });
        return findById(id);
    }

    /**
     * O instalador é opcional: quando não informado, grava NULL explicitamente
     * em vez de confiar na conversão automática do driver.
     */
    private void definirInstalador(PreparedStatement ps, int indice, Long instaladorId) throws SQLException {
        if (instaladorId == null) {
            ps.setNull(indice, Types.BIGINT);
        } else {
            ps.setLong(indice, instaladorId);
        }
    }

    public Porta delete(Long id) {
        Porta porta = findById(id);
        if (porta == null) {
            return null;
        }
        jdbcTemplate.update("DELETE FROM porta WHERE id = ?", id);
        return porta;
    }

    public Porta updateStage(Long id, EtapaPorta etapa, boolean concluida) {
        jdbcTemplate.update("UPDATE porta SET " + coluna(etapa) + " = ? WHERE id = ?", concluida, id);
        return findById(id);
    }

    /**
     * Traduz a etapa do checklist para a coluna correspondente na tabela porta,
     * evitando concatenar nomes vindos da requisição.
     */
    private String coluna(EtapaPorta etapa) {
        return switch (etapa) {
            case MONTAGEM -> "montagem";
            case FIXACAO -> "fixacao";
            case FECHADURA -> "fechadura";
            case VISTAS -> "vistas";
            case ACABAMENTO -> "acabamento";
        };
    }

    private Porta mapPorta(ResultSet rs, int rowNum) throws SQLException {
        Porta porta = new Porta(rs.getLong("id"), rs.getString("local"), rs.getLong("apartamento_id"));
        long instaladorId = rs.getLong("instalador_id");
        porta.setInstaladorId(rs.wasNull() ? null : instaladorId);
        for (EtapaPorta etapa : EtapaPorta.values()) {
            porta.getEtapas().put(etapa.getNome(), rs.getBoolean(coluna(etapa)));
        }
        return porta;
    }
}
