package com.cursojava.ObraControl.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cursojava.ObraControl.model.Obra;

@Repository
public class ObraRepository {

    private final JdbcTemplate jdbcTemplate;

    public ObraRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_BASE = """
            SELECT obra.id, obra.nome, obra.endereco,
                   construtora.nome AS construtora,
                   cidade.nome AS cidade
            FROM obra
            JOIN construtora ON construtora.id = obra.construtora_id
            JOIN cidade ON cidade.id = obra.cidade_id
            """;

    public List<Obra> listarTodas() {
        return jdbcTemplate.query(SELECT_BASE + " ORDER BY obra.id",
                (rs, rowNum) -> new Obra(rs.getLong("id"), rs.getString("nome"),
                        rs.getString("construtora"), rs.getString("cidade"), rs.getString("endereco")));
    }

    public Obra buscarPorId(Long id) {
        List<Obra> obras = jdbcTemplate.query(SELECT_BASE + " WHERE obra.id = ?",
                (rs, rowNum) -> new Obra(rs.getLong("id"), rs.getString("nome"),
                        rs.getString("construtora"), rs.getString("cidade"), rs.getString("endereco")),
                id);
        return obras.isEmpty() ? null : obras.get(0);
    }

    public Obra salvar(Obra obra) {
        Long construtoraId = resolverConstrutora(obra.getConstrutora());
        Long cidadeId = resolverCidade(obra.getCidade());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO obra (nome, cidade_id, construtora_id, endereco) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, obra.getNome());
            ps.setLong(2, cidadeId);
            ps.setLong(3, construtoraId);
            ps.setString(4, obra.getEndereco());
            return ps;
        }, keyHolder);

        obra.setId(keyHolder.getKey().longValue());
        return obra;
    }

    private Long resolverConstrutora(String nome) {
        List<Long> ids = jdbcTemplate.queryForList(
                "SELECT id FROM construtora WHERE nome = ? LIMIT 1", Long.class, nome);
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        jdbcTemplate.update("INSERT INTO construtora (nome) VALUES (?)", nome);
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private Long resolverCidade(String nome) {
        List<Long> ids = jdbcTemplate.queryForList(
                "SELECT id FROM cidade WHERE nome = ? LIMIT 1", Long.class, nome);
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        jdbcTemplate.update("INSERT INTO cidade (nome, estado_id) VALUES (?, 1)", nome);
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    public Obra excluir(Long id) {
        Obra obra = buscarPorId(id);
        if (obra == null) {
            return null;
        }
        jdbcTemplate.update("DELETE FROM obra WHERE id = ?", id);
        return obra;
    }
}