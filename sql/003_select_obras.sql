SELECT
    o.id,
    o.nome,
    o.endereco,
    o.status,
    c.nome AS cidade,
    e.sigla AS estado,
    ct.nome AS construtora
FROM obra o
INNER JOIN cidade c
    ON c.id = o.cidade_id
INNER JOIN estado e
    ON e.id = c.estado_id
INNER JOIN construtora ct
    ON ct.id = o.construtora_id
ORDER BY o.id;
