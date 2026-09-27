package com.cursojava.ObraControl.dto;

public record ResumoDashboard(
        long totalObras,
        long obrasAbertas,
        long obrasFinalizadas,
        long totalApartamentos,
        long totalPortas,
        long portasConcluidas,
        long portasSemInstalador,
        long totalInstaladores) {
}
