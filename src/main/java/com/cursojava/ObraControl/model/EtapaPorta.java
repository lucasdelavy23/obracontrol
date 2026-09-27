package com.cursojava.ObraControl.model;

/**
 * Etapas do checklist de instalação de uma porta, na ordem de execução.
 */
public enum EtapaPorta {
    MONTAGEM("montagem"),
    FIXACAO("fixacao"),
    FECHADURA("fechadura"),
    VISTAS("vistas"),
    ACABAMENTO("acabamento");

    private final String nome;

    EtapaPorta(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public static EtapaPorta fromNome(String nome) {
        for (EtapaPorta etapa : values()) {
            if (etapa.nome.equalsIgnoreCase(nome)) {
                return etapa;
            }
        }
        return null;
    }
}
