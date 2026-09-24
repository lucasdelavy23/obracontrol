-- ============================================
-- ESTADO
-- ============================================

INSERT INTO estado (nome, sigla) VALUES
('Santa Catarina', 'SC'),
('Paraná', 'PR'),
('Rio Grande do Sul', 'RS'),
('São Paulo', 'SP'),
('Rio de Janeiro', 'RJ');


-- ============================================
-- CIDADE
-- ============================================

INSERT INTO cidade (nome, estado_id) VALUES
('Florianópolis', 1),
('Joinville', 1),
('Curitiba', 2),
('Porto Alegre', 3),
('São Paulo', 4);


-- ============================================
-- CONSTRUTORA
-- ============================================

INSERT INTO construtora (nome) VALUES
('Dallo'),
('Pascoalotto'),
('Procave'),
('FG'),
('AS Ramos');


-- ============================================
-- INSTALADOR
-- ============================================

INSERT INTO instalador (nome, telefone) VALUES
('Carlos Eduardo Silva', '(48) 99999-1001'),
('João Pedro Santos', '(48) 99999-1002'),
('Marcos Antônio Oliveira', '(41) 99999-1003'),
('Rafael Souza Costa', '(51) 99999-1004'),
('Fernando Alves Pereira', '(11) 99999-1005');


-- ============================================
-- OBRA
-- ============================================

INSERT INTO obra (
    nome,
    cidade_id,
    construtora_id,
    endereco,
    status
) VALUES
(
    'Residencial Atlântico',
    1,
    1,
    'Rua das Palmeiras, 100 - Centro',
    'aberta'
),
(
    'Condomínio Jardim Europa',
    2,
    2,
    'Avenida Brasil, 1500 - América',
    'aberta'
),
(
    'Residencial Parque Sul',
    3,
    3,
    'Rua XV de Novembro, 800 - Centro',
    'aberta'
),
(
    'Edifício Vista Alegre',
    4,
    4,
    'Avenida Ipiranga, 2500 - Menino Deus',
    'finalizada'
),
(
    'Residencial Nova Paulista',
    5,
    5,
    'Rua Augusta, 1200 - Consolação',
    'aberta'
);


-- ============================================
-- APARTAMENTO
-- ============================================

INSERT INTO apartamento (
    numero,
    quantidade_portas,
    obra_id
) VALUES
('101', 5, 1),
('102', 4, 1),
('201', 6, 2),
('202', 5, 2),
('301', 7, 3),
('302', 6, 3),
('401', 5, 4),
('402', 5, 4),
('501', 8, 5),
('502', 7, 5);


-- ============================================
-- PORTA
-- ============================================

INSERT INTO porta (
    local,
    apartamento_id,
    instalador_id,
    montagem,
    fixacao,
    fechadura,
    vistas,
    acabamento
) VALUES

-- Apartamento 101
('Entrada', 1, 1, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 1', 1, 1, TRUE, TRUE, TRUE, TRUE, FALSE),
('Quarto 2', 1, 2, TRUE, TRUE, FALSE, FALSE, FALSE),
('Banheiro', 1, 2, TRUE, TRUE, TRUE, FALSE, FALSE),
('Área de serviço', 1, 1, TRUE, FALSE, FALSE, FALSE, FALSE),

-- Apartamento 102
('Entrada', 2, 2, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 1', 2, 2, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 2', 2, 3, TRUE, TRUE, TRUE, FALSE, FALSE),
('Banheiro', 2, 3, TRUE, TRUE, FALSE, FALSE, FALSE),

-- Apartamento 201
('Entrada', 3, 3, TRUE, TRUE, TRUE, TRUE, TRUE),
('Sala', 3, 3, TRUE, TRUE, TRUE, TRUE, FALSE),
('Quarto 1', 3, 4, TRUE, TRUE, TRUE, FALSE, FALSE),
('Quarto 2', 3, 4, TRUE, FALSE, FALSE, FALSE, FALSE),
('Quarto 3', 3, 5, TRUE, TRUE, TRUE, TRUE, TRUE),
('Banheiro', 3, 5, TRUE, TRUE, FALSE, FALSE, FALSE),

-- Apartamento 202
('Entrada', 4, 1, TRUE, TRUE, TRUE, TRUE, TRUE),
('Sala', 4, 1, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 1', 4, 2, TRUE, TRUE, TRUE, FALSE, FALSE),
('Quarto 2', 4, 2, TRUE, TRUE, FALSE, FALSE, FALSE),
('Banheiro', 4, 3, TRUE, FALSE, FALSE, FALSE, FALSE),

-- Apartamento 301
('Entrada', 5, 3, TRUE, TRUE, TRUE, TRUE, TRUE),
('Sala', 5, 3, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 1', 5, 4, TRUE, TRUE, TRUE, TRUE, FALSE),
('Quarto 2', 5, 4, TRUE, TRUE, FALSE, FALSE, FALSE),
('Quarto 3', 5, 5, TRUE, FALSE, FALSE, FALSE, FALSE),
('Banheiro 1', 5, 5, TRUE, TRUE, TRUE, FALSE, FALSE),
('Banheiro 2', 5, 1, TRUE, TRUE, TRUE, TRUE, TRUE),

-- Apartamento 302
('Entrada', 6, 1, TRUE, TRUE, TRUE, TRUE, TRUE),
('Sala', 6, 1, TRUE, TRUE, TRUE, TRUE, FALSE),
('Quarto 1', 6, 2, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 2', 6, 2, TRUE, TRUE, FALSE, FALSE, FALSE),
('Quarto 3', 6, 3, TRUE, TRUE, FALSE, FALSE, FALSE),
('Banheiro', 6, 3, TRUE, FALSE, FALSE, FALSE, FALSE),

-- Apartamento 401
('Entrada', 7, 4, TRUE, TRUE, TRUE, TRUE, TRUE),
('Sala', 7, 4, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 1', 7, 5, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 2', 7, 5, TRUE, TRUE, TRUE, TRUE, TRUE),
('Banheiro', 7, 1, TRUE, TRUE, TRUE, TRUE, TRUE),

-- Apartamento 402
('Entrada', 8, 1, TRUE, TRUE, TRUE, TRUE, TRUE),
('Sala', 8, 1, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 1', 8, 2, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 2', 8, 2, TRUE, TRUE, TRUE, TRUE, TRUE),
('Banheiro', 8, 3, TRUE, TRUE, TRUE, TRUE, TRUE),

-- Apartamento 501
('Entrada', 9, 3, TRUE, TRUE, TRUE, TRUE, TRUE),
('Sala', 9, 3, TRUE, TRUE, TRUE, TRUE, FALSE),
('Quarto 1', 9, 4, TRUE, TRUE, TRUE, TRUE, FALSE),
('Quarto 2', 9, 4, TRUE, TRUE, FALSE, FALSE, FALSE),
('Quarto 3', 9, 5, TRUE, TRUE, FALSE, FALSE, FALSE),
('Quarto 4', 9, 5, TRUE, FALSE, FALSE, FALSE, FALSE),
('Banheiro 1', 9, 1, TRUE, TRUE, TRUE, TRUE, TRUE),
('Banheiro 2', 9, 1, TRUE, TRUE, TRUE, FALSE, FALSE),

-- Apartamento 502
('Entrada', 10, 2, TRUE, TRUE, TRUE, TRUE, TRUE),
('Sala', 10, 2, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 1', 10, 3, TRUE, TRUE, TRUE, TRUE, TRUE),
('Quarto 2', 10, 3, TRUE, TRUE, TRUE, TRUE, FALSE),
('Quarto 3', 10, 4, TRUE, TRUE, FALSE, FALSE, FALSE),
('Quarto 4', 10, 4, TRUE, FALSE, FALSE, FALSE, FALSE),
('Banheiro', 10, 5, TRUE, TRUE, TRUE, TRUE, TRUE);