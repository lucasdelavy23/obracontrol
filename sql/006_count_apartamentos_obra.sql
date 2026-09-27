SELECT
    o.id,
    o.nome,
    c.nome AS cidade,
    e.sigla AS estado,
    COUNT(a.id) AS apartamentos
FROM obra o
INNER JOIN cidade c
    ON c.id = o.cidade_id
INNER JOIN estado e
    ON e.id = c.estado_id
LEFT JOIN apartamento a
    ON a.obra_id = o.id
GROUP BY
    o.id,
    o.nome,
    c.nome,
    e.sigla
ORDER BY o.nome;
