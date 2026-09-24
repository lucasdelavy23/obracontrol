-- ============================================
-- 1. ESTADO
-- ============================================

CREATE TABLE estado (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    sigla CHAR(2) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_estado_sigla (sigla)
) ENGINE=InnoDB;


-- ============================================
-- 2. CIDADE
-- ============================================

CREATE TABLE cidade (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    estado_id BIGINT UNSIGNED NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_cidade_estado
        FOREIGN KEY (estado_id)
        REFERENCES estado (id),

    UNIQUE KEY uk_cidade_nome_estado (nome, estado_id)
) ENGINE=InnoDB;


-- ============================================
-- 3. CONSTRUTORA
-- ============================================

CREATE TABLE construtora (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,

    PRIMARY KEY (id)
) ENGINE=InnoDB;


-- ============================================
-- 4. INSTALADOR
-- ============================================

CREATE TABLE instalador (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    telefone VARCHAR(30),

    PRIMARY KEY (id)
) ENGINE=InnoDB;


-- ============================================
-- 5. OBRA
-- ============================================

CREATE TABLE obra (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    cidade_id BIGINT UNSIGNED NOT NULL,
    construtora_id BIGINT UNSIGNED NOT NULL,
    endereco VARCHAR(300),
    status ENUM('aberta', 'finalizada') NOT NULL DEFAULT 'aberta',

    PRIMARY KEY (id),

    CONSTRAINT fk_obra_cidade
        FOREIGN KEY (cidade_id)
        REFERENCES cidade (id),

    CONSTRAINT fk_obra_construtora
        FOREIGN KEY (construtora_id)
        REFERENCES construtora (id),

    INDEX idx_obra_cidade (cidade_id),
    INDEX idx_obra_construtora (construtora_id),
    INDEX idx_obra_status (status)
) ENGINE=InnoDB;


-- ============================================
-- 6. APARTAMENTO
-- ============================================

CREATE TABLE apartamento (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    numero VARCHAR(50) NOT NULL,
    quantidade_portas INT UNSIGNED NOT NULL DEFAULT 0,
    obra_id BIGINT UNSIGNED NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_apartamento_obra
        FOREIGN KEY (obra_id)
        REFERENCES obra (id)
        ON DELETE CASCADE,

    UNIQUE KEY uk_apartamento_numero_obra (obra_id, numero),
    INDEX idx_apartamento_obra (obra_id)
) ENGINE=InnoDB;


-- ============================================
-- 7. PORTA
-- ============================================

CREATE TABLE porta (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    local VARCHAR(200) NOT NULL,
    apartamento_id BIGINT UNSIGNED NOT NULL,
    instalador_id BIGINT UNSIGNED NULL,

    montagem BOOLEAN NOT NULL DEFAULT FALSE,
    fixacao BOOLEAN NOT NULL DEFAULT FALSE,
    fechadura BOOLEAN NOT NULL DEFAULT FALSE,
    vistas BOOLEAN NOT NULL DEFAULT FALSE,
    acabamento BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id),

    CONSTRAINT fk_porta_apartamento
        FOREIGN KEY (apartamento_id)
        REFERENCES apartamento (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_porta_instalador
        FOREIGN KEY (instalador_id)
        REFERENCES instalador (id)
        ON DELETE SET NULL,

    INDEX idx_porta_apartamento (apartamento_id),
    INDEX idx_porta_instalador (instalador_id)
) ENGINE=InnoDB;