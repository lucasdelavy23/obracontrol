package com.cursojava.ObraControl.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cursojava.ObraControl.dto.CargaInstalador;
import com.cursojava.ObraControl.dto.ProgressoObra;
import com.cursojava.ObraControl.dto.ResumoDashboard;

@Repository
public class DashboardRepository {

    private static final String ETAPAS_CONCLUIDAS = """
            CASE WHEN montagem = TRUE THEN 1 ELSE 0 END +
            CASE WHEN fixacao = TRUE THEN 1 ELSE 0 END +
            CASE WHEN fechadura = TRUE THEN 1 ELSE 0 END +
            CASE WHEN vistas = TRUE THEN 1 ELSE 0 END +
            CASE WHEN acabamento = TRUE THEN 1 ELSE 0 END
            """;

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ResumoDashboard getResumo() {
        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM obra) AS total_obras,
                    (SELECT COUNT(*) FROM obra WHERE status = 'aberta') AS obras_abertas,
                    (SELECT COUNT(*) FROM obra WHERE status = 'finalizada') AS obras_finalizadas,
                    (SELECT COUNT(*) FROM apartamento) AS total_apartamentos,
                    (SELECT COUNT(*) FROM porta) AS total_portas,
                    (SELECT COUNT(*) FROM porta
                        WHERE montagem = TRUE AND fixacao = TRUE AND fechadura = TRUE
                        AND vistas = TRUE AND acabamento = TRUE) AS portas_concluidas,
                    (SELECT COUNT(*) FROM porta WHERE instalador_id IS NULL) AS portas_sem_instalador,
                    (SELECT COUNT(*) FROM instalador) AS total_instaladores,
                    (SELECT COALESCE(SUM(%s), 0) FROM porta) AS etapas_concluidas
                """.formatted(ETAPAS_CONCLUIDAS);

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ResumoDashboard(
                rs.getLong("total_obras"),
                rs.getLong("obras_abertas"),
                rs.getLong("obras_finalizadas"),
                rs.getLong("total_apartamentos"),
                rs.getLong("total_portas"),
                rs.getLong("portas_concluidas"),
                rs.getLong("portas_sem_instalador"),
                rs.getLong("total_instaladores"),
                rs.getLong("etapas_concluidas")));
    }

    public List<ProgressoObra> getProgressoObras() {
        String sql = """
                SELECT obra.id, obra.nome, obra.status,
                       COUNT(DISTINCT apartamento.id) AS quantidade_apartamentos,
                       COUNT(porta.id) AS quantidade_portas,
                       COALESCE(SUM(%s), 0) AS etapas_concluidas
                FROM obra
                LEFT JOIN apartamento ON apartamento.obra_id = obra.id
                LEFT JOIN porta ON porta.apartamento_id = apartamento.id
                GROUP BY obra.id, obra.nome, obra.status
                ORDER BY obra.nome
                """.formatted(ETAPAS_CONCLUIDAS);

        return jdbcTemplate.query(sql, (rs, rowNum) -> new ProgressoObra(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("status"),
                rs.getLong("quantidade_apartamentos"),
                rs.getLong("quantidade_portas"),
                rs.getLong("etapas_concluidas")));
    }

    public List<CargaInstalador> getCargaInstaladores() {
        return jdbcTemplate.query("""
                SELECT instalador.id, instalador.nome, COUNT(porta.id) AS quantidade_portas
                FROM instalador
                LEFT JOIN porta ON porta.instalador_id = instalador.id
                GROUP BY instalador.id, instalador.nome
                ORDER BY quantidade_portas DESC, instalador.nome
                """, (rs, rowNum) -> new CargaInstalador(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getLong("quantidade_portas")));
    }
}
