package com.cursojava.ObraControl.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cursojava.ObraControl.model.Obra;
import com.cursojava.ObraControl.model.StatusObra;
import com.cursojava.ObraControl.dto.CadastroObra;

@Repository
public class ObraRepository {

    private final JdbcTemplate jdbcTemplate;

    public ObraRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_BASE = """
            SELECT obra.id, obra.nome, obra.endereco, obra.status,
                   obra.cidade_id, obra.construtora_id, cidade.estado_id,
                   construtora.nome AS construtora,
                   cidade.nome AS cidade
            FROM obra
            JOIN construtora ON construtora.id = obra.construtora_id
            JOIN cidade ON cidade.id = obra.cidade_id
            """;

    public List<Obra> listarTodas() {
        return jdbcTemplate.query(SELECT_BASE + " ORDER BY obra.id",
                this::mapObra);
    }

    public Obra buscarPorId(Long id) {
        List<Obra> obras = jdbcTemplate.query(SELECT_BASE + " WHERE obra.id = ?",
                this::mapObra,
                id);
        return obras.isEmpty() ? null : obras.get(0);
    }

    public Obra save(CadastroObra obra) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO obra (nome, cidade_id, construtora_id, endereco) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, obra.nome());
            ps.setLong(2, obra.cidadeId());
            ps.setLong(3, obra.construtoraId());
            ps.setString(4, obra.endereco());
            return ps;
        }, keyHolder);

        return buscarPorId(keyHolder.getKey().longValue());
    }

    private Obra mapObra(ResultSet rs, int rowNum) throws SQLException {
        Obra obra = new Obra(rs.getLong("id"), rs.getString("nome"), rs.getString("construtora"),
                rs.getString("cidade"), rs.getString("endereco"));
        obra.setCidadeId(rs.getLong("cidade_id"));
        obra.setEstadoId(rs.getLong("estado_id"));
        obra.setConstrutoraId(rs.getLong("construtora_id"));
        obra.setStatus(StatusObra.fromNome(rs.getString("status")));
        return obra;
    }

    public Obra update(Long id, CadastroObra obra) {
        jdbcTemplate.update("UPDATE obra SET nome = ?, cidade_id = ?, construtora_id = ?, endereco = ? WHERE id = ?",
                obra.nome(), obra.cidadeId(), obra.construtoraId(), obra.endereco(), id);
        return buscarPorId(id);
    }

    public Obra updateStatus(Long id, StatusObra status) {
        jdbcTemplate.update("UPDATE obra SET status = ? WHERE id = ?", status.getNome(), id);
        return buscarPorId(id);
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
