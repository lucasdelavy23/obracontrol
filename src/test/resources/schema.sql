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