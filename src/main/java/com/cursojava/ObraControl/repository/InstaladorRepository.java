package com.cursojava.ObraControl.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cursojava.ObraControl.model.Instalador;

@Repository
public class InstaladorRepository {

    private final JdbcTemplate jdbcTemplate;

    public InstaladorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Instalador> listarTodas() {
        return jdbcTemplate.query("SELECT id, nome, telefone FROM instalador ORDER BY id",
                (rs, rowNum) -> new Instalador(rs.getLong("id"), rs.getString("nome"), rs.getString("telefone")));
    }

    public Instalador buscarPorId(Long id) {
        List<Instalador> instaladores = jdbcTemplate.query(
                "SELECT id, nome, telefone FROM instalador WHERE id = ?",
                (rs, rowNum) -> new Instalador(rs.getLong("id"), rs.getString("nome"), rs.getString("telefone")),
                id);
        return instaladores.isEmpty() ? null : instaladores.get(0);
    }

    public Instalador salvar(Instalador instalador) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO instalador (nome, telefone) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, instalador.getNome());
            ps.setString(2, instalador.getTelefone());
            return ps;
        }, keyHolder);

        instalador.setId(keyHolder.getKey().longValue());
        return instalador;
    }

    public Instalador excluir(Long id) {
        Instalador instalador = buscarPorId(id);
        if (instalador == null) {
            return null;
        }
        jdbcTemplate.update("DELETE FROM instalador WHERE id = ?", id);
        return instalador;
    }

    public Instalador update(Long id, Instalador instalador) {
        jdbcTemplate.update("UPDATE instalador SET nome = ?, telefone = ? WHERE id = ?",
                instalador.getNome(), instalador.getTelefone(), id);
        return buscarPorId(id);
    }
}
