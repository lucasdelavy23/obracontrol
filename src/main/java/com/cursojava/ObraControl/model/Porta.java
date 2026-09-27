package com.cursojava.ObraControl.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Porta {
    private Long id;
    private String local;
    private Long apartamentoId;
    private Long instaladorId;
    private Map<String, Boolean> etapas = new LinkedHashMap<>();

    public Porta() {
        for (EtapaPorta etapa : EtapaPorta.values()) {
            etapas.put(etapa.getNome(), false);
        }
    }

    public Porta(Long id, String local, Long apartamentoId) {
        this();
        this.id = id;
        this.local = local;
        this.apartamentoId = apartamentoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Long getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(Long apartamentoId) {
        this.apartamentoId = apartamentoId;
    }

    public Long getInstaladorId() {
        return instaladorId;
    }

    public void setInstaladorId(Long instaladorId) {
        this.instaladorId = instaladorId;
    }

    public Map<String, Boolean> getEtapas() {
        return etapas;
    }

    public void setEtapas(Map<String, Boolean> etapas) {
        this.etapas = etapas;
    }
}
