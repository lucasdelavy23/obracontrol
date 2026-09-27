package com.cursojava.ObraControl.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cursojava.ObraControl.dto.CadastroApartamento;
import com.cursojava.ObraControl.model.Apartamento;

@Repository
public class ApartamentoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApartamentoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_BASE = """
            SELECT apartamento.id, apartamento.numero, apartamento.quantidade_portas,
                   apartamento.obra_id, obra.nome AS obra
            FROM apartamento
            JOIN obra ON obra.id = apartamento.obra_id
            """;

    public List<Apartamento> findByObra(Long obraId) {
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE apartamento.obra_id = ? ORDER BY apartamento.numero, apartamento.id",
                this::mapApartamento, obraId);
    }

    public Apartamento findById(Long id) {
        List<Apartamento> apartamentos = jdbcTemplate.query(SELECT_BASE + " WHERE apartamento.id = ?",
                this::mapApartamento, id);
        return apartamentos.isEmpty() ? null : apartamentos.get(0);
    }

    public Apartamento save(CadastroApartamento cadastro) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO apartamento (numero, obra_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cadastro.numero());
            ps.setLong(2, cadastro.obraId());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        atualizarQuantidadePortas(id);
        return findById(id);
    }

    public Apartamento update(Long id, CadastroApartamento cadastro) {
        jdbcTemplate.update("UPDATE apartamento SET numero = ?, obra_id = ? WHERE id = ?",
                cadastro.numero(), cadastro.obraId(), id);
        atualizarQuantidadePortas(id);
        return findById(id);
    }

    public Apartamento delete(Long id) {
        Apartamento apartamento = findById(id);
        if (apartamento == null) {
            return null;
        }
        jdbcTemplate.update("DELETE FROM apartamento WHERE id = ?", id);
        return apartamento;
    }

    public boolean existsByNumero(Long obraId, String numero, Long excludeId) {
        Long count = excludeId == null
                ? jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM apartamento WHERE obra_id = ? AND numero = ?", Long.class, obraId, numero)
                : jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM apartamento WHERE obra_id = ? AND numero = ? AND id <> ?",
                        Long.class, obraId, numero, excludeId);
        return count != null && count > 0;
    }

    /**
     * Mantém quantidade_portas alinhada com as portas efetivamente cadastradas.
     */
    public void atualizarQuantidadePortas(Long id) {
        jdbcTemplate.update("""
                UPDATE apartamento
                SET quantidade_portas = (SELECT COUNT(*) FROM porta WHERE apartamento_id = ?)
                WHERE id = ?
                """, id, id);
    }

    private Apartamento mapApartamento(ResultSet rs, int rowNum) throws SQLException {
        Apartamento apartamento = new Apartamento(rs.getLong("id"), rs.getString("numero"));
        apartamento.setObraId(rs.getLong("obra_id"));
        apartamento.setObra(rs.getString("obra"));
        apartamento.setQuantidadePortas(rs.getInt("quantidade_portas"));
        return apartamento;
    }
}
