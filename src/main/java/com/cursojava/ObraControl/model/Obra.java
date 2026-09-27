package com.cursojava.ObraControl.model;

import java.util.ArrayList;
import java.util.List;

public class Obra {
    private Long id;
    private String nome;
    private String construtora;
    private String cidade;
    private String endereco;
    private Long cidadeId;
    private Long estadoId;
    private Long construtoraId;
    private List<Apartamento> apartamentos = new ArrayList<>();

    public Obra() {
    }

    public Obra(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Obra(Long id, String nome, String construtora, String cidade, String endereco) {
        this.id = id;
        this.nome = nome;
        this.construtora = construtora;
        this.cidade = cidade;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public Long getCidadeId() {
        return cidadeId;
    }

    public void setCidadeId(Long cidadeId) {
        this.cidadeId = cidadeId;
    }

    public Long getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }

    public Long getConstrutoraId() {
        return construtoraId;
    }

    public void setConstrutoraId(Long construtoraId) {
        this.construtoraId = construtoraId;
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

    public String getConstrutora() {
        return construtora;
    }

    public void setConstrutora(String construtora) {
        this.construtora = construtora;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<Apartamento> getApartamentos() {
        return apartamentos;
    }

    public void setApartamentos(List<Apartamento> apartamentos) {
        this.apartamentos = apartamentos;
    }
}
