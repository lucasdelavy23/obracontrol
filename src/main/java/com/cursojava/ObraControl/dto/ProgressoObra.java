package com.cursojava.ObraControl.dto;

public record ProgressoObra(
        Long id,
        String nome,
        String status,
        long quantidadeApartamentos,
        long quantidadePortas,
        long etapasConcluidas) {

    public long getTotalEtapas() {
        return quantidadePortas * 5;
    }

    public int getPercentual() {
        long total = getTotalEtapas();
        return total == 0 ? 0 : (int) Math.round(etapasConcluidas * 100.0 / total);
    }
}
