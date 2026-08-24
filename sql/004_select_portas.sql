SELECT
    p.id,
    p.local,
    a.numero AS apartamento,
    o.nome AS obra,
    i.nome AS instalador,
    p.montagem,
    p.fixacao,
    p.fechadura,
    p.vistas,
    p.acabamento
FROM porta p
INNER JOIN apartamento a
    ON a.id = p.apartamento_id
INNER JOIN obra o
    ON o.id = a.obra_id
LEFT JOIN instalador i
    ON i.id = p.instalador_id
WHERE (o.id = 1)
ORDER BY
    o.id,
    a.numero,
    p.id;
