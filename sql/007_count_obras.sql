SELECT
    COUNT(*) AS total_obras,
    SUM(CASE WHEN status = 'aberta' THEN 1 ELSE 0 END) AS obras_abertas,
    SUM(CASE WHEN status = 'finalizada' THEN 1 ELSE 0 END) AS obras_finalizadas
FROM obra;
