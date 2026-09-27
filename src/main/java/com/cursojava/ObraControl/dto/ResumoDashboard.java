package com.cursojava.ObraControl.dto;

public record ResumoDashboard(
        long totalObras,
        long obrasAbertas,
        long obrasFinalizadas,
        long totalApartamentos,
        long totalPortas,
        long portasConcluidas,
        long portasSemInstalador,
        long totalInstaladores,
        long etapasConcluidas) {

    public long getTotalEtapas() {
        return totalPortas * 5;
    }

    public int getPercentualGeral() {
        return calcularPercentual(etapasConcluidas, getTotalEtapas());
    }

    private int calcularPercentual(long concluidas, long total) {
        return total == 0 ? 0 : (int) Math.round(concluidas * 100.0 / total);
    }
}
