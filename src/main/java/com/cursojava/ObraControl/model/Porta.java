package com.cursojava.ObraControl.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Porta {
    private Long id;
    private String localizacao;

    private Map<String, Boolean> etapas = new LinkedHashMap<>();

    public Porta() {

        this.etapas.put("Montagem", false);
        this.etapas.put("Fixar", false);
        this.etapas.put("Vistas", false);
        this.etapas.put("Fechadura", false);
        this.etapas.put("Acabamento", false);
    }

    public Porta(Long id, String localizacao) {
        this();
        this.id = id;
        this.localizacao = localizacao;
    }

    public Long getId() {
        return id;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Map<String, Boolean> getEtapas() {
        return etapas;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public void setEtapas(Map<String, Boolean> etapas) {
        this.etapas = etapas;
    }

}
