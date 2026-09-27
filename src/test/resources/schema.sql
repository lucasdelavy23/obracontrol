CREATE TABLE estado (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    sigla CHAR(2) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE cidade (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    estado_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_cidade_estado FOREIGN KEY (estado_id) REFERENCES estado (id)
);

CREATE TABLE construtora (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE instalador (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    telefone VARCHAR(30),
    PRIMARY KEY (id)
);

CREATE TABLE obra (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    cidade_id BIGINT NOT NULL,
    construtora_id BIGINT NOT NULL,
    endereco VARCHAR(300),
    status VARCHAR(20) NOT NULL DEFAULT 'aberta',
    PRIMARY KEY (id),
    CONSTRAINT fk_obra_cidade FOREIGN KEY (cidade_id) REFERENCES cidade (id),
    CONSTRAINT fk_obra_construtora FOREIGN KEY (construtora_id) REFERENCES construtora (id)
);

CREATE TABLE apartamento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(50) NOT NULL,
    quantidade_portas INT NOT NULL DEFAULT 0,
    obra_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_apartamento_obra FOREIGN KEY (obra_id) REFERENCES obra (id) ON DELETE CASCADE,
    CONSTRAINT uk_apartamento_numero_obra UNIQUE (obra_id, numero)
);

CREATE TABLE porta (
    id BIGINT NOT NULL AUTO_INCREMENT,
    local VARCHAR(200) NOT NULL,
    apartamento_id BIGINT NOT NULL,
    instalador_id BIGINT,
    montagem BOOLEAN NOT NULL DEFAULT FALSE,
    fixacao BOOLEAN NOT NULL DEFAULT FALSE,
    fechadura BOOLEAN NOT NULL DEFAULT FALSE,
    vistas BOOLEAN NOT NULL DEFAULT FALSE,
    acabamento BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_porta_apartamento FOREIGN KEY (apartamento_id) REFERENCES apartamento (id) ON DELETE CASCADE,
    CONSTRAINT fk_porta_instalador FOREIGN KEY (instalador_id) REFERENCES instalador (id) ON DELETE SET NULL
);