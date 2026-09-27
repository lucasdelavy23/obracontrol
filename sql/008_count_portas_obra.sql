SELECT
    o.id,
    o.nome,
    COUNT(p.id) AS portas
FROM obra o
LEFT JOIN apartamento a
    ON a.obra_id = o.id
LEFT JOIN porta p
    ON p.apartamento_id = a.id
GROUP BY
    o.id,
    o.nome
ORDER BY o.nome;
