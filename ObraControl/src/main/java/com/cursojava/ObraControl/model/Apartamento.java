package com.cursojava.ObraControl.model;

import java.util.ArrayList;
import java.util.List;

public class Apartamento {
    private Long id;
    private String numero;
    private List<Porta> portas = new ArrayList<>();

    public Apartamento() {
    }

    public Apartamento(Long id, String numero) {
        this.id = id;
        this.numero = numero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<Porta> getPortas() {
        return portas;
    }

    public void setPortas(List<Porta> portas) {
        this.portas = portas;
    }
}