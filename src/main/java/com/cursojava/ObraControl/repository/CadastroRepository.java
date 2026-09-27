package com.cursojava.ObraControl.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cursojava.ObraControl.dto.CidadeResumo;
import com.cursojava.ObraControl.dto.ConstrutoraResumo;
import com.cursojava.ObraControl.dto.EstadoResumo;

@Repository
public class CadastroRepository {
    private final JdbcTemplate jdbcTemplate;

    public CadastroRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EstadoResumo> findStates() {
        return jdbcTemplate.query("SELECT id, nome, sigla FROM estado ORDER BY nome",
                (rs, rowNum) -> new EstadoResumo(rs.getLong("id"), rs.getString("nome"), rs.getString("sigla")));
    }

    public List<CidadeResumo> findCitiesByState(Long estadoId) {
        return jdbcTemplate.query("SELECT id, nome, estado_id FROM cidade WHERE estado_id = ? ORDER BY nome",
                (rs, rowNum) -> new CidadeResumo(rs.getLong("id"), rs.getString("nome"), rs.getLong("estado_id")),
                estadoId);
    }

    public List<ConstrutoraResumo> findBuilders() {
        return jdbcTemplate.query("SELECT id, nome FROM construtora ORDER BY nome",
                (rs, rowNum) -> new ConstrutoraResumo(rs.getLong("id"), rs.getString("nome")));
    }

    public boolean stateExists(Long id) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM estado WHERE id = ?", Long.class, id) > 0;
    }

    public boolean cityExists(Long id) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cidade WHERE id = ?", Long.class, id) > 0;
    }

    public boolean builderExists(Long id) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM construtora WHERE id = ?", Long.class, id) > 0;
    }
}
