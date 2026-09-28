package com.cursojava.ObraControl.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusObra {
    ABERTA("aberta"),
    FINALIZADA("finalizada");

    private final String nome;

    StatusObra(String nome) {
        this.nome = nome;
    }

    @JsonValue
    public String getNome() {
        return nome;
    }

    public static StatusObra fromNome(String nome) {
        if (nome != null) {
            for (StatusObra status : values()) {
                if (status.nome.equalsIgnoreCase(nome.strip())) {
                    return status;
                }
            }
        }
        return null;
    }
}
