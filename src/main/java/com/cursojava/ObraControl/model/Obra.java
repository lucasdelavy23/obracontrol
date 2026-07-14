package com.cursojava.ObraControl.model;

import java.util.ArrayList;
import java.util.List;

public class Obra {
    private Long id;
    private String nome;
    private List<Apartamento> apartamentos = new ArrayList<>();

    public Obra() {
    }

    public Obra(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Apartamento> getApartamentos() {
        return apartamentos;
    }

    public void setApartamentos(List<Apartamento> apartamentos) {
        this.apartamentos = apartamentos;
    }
}
