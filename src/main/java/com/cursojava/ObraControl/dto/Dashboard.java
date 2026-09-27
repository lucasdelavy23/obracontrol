package com.cursojava.ObraControl.dto;

import java.util.List;

public record Dashboard(
        ResumoDashboard resumo,
        List<ProgressoObra> progressoObras,
        List<CargaInstalador> cargaInstaladores) {
}
