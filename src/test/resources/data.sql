INSERT INTO estado (id, nome, sigla) VALUES (1, 'Santa Catarina', 'SC');

INSERT INTO cidade (nome, estado_id) VALUES ('Itapema', 1);

INSERT INTO construtora (nome) VALUES ('Dallo');

INSERT INTO instalador (nome, telefone) VALUES ('Carlos Eduardo Silva', '(48) 99999-1001');

INSERT INTO obra (nome, cidade_id, construtora_id, endereco)
VALUES ('Residencial Atlântico', 1, 1, 'Rua das Palmeiras, 100 - Centro');