SELECT
    i.id,
    i.nome,
    i.telefone,
    COUNT(p.id) AS quantidade_portas
FROM instalador i
LEFT JOIN porta p
    ON p.instalador_id = i.id
GROUP BY
    i.id,
    i.nome,
    i.telefone
ORDER BY i.nome;
