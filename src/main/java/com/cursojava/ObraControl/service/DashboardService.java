package com.cursojava.ObraControl.service;

import org.springframework.stereotype.Service;

import com.cursojava.ObraControl.dto.Dashboard;
import com.cursojava.ObraControl.repository.DashboardRepository;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public Dashboard getDashboard() {
        return new Dashboard(
                dashboardRepository.getResumo(),
                dashboardRepository.getProgressoObras(),
                dashboardRepository.getCargaInstaladores());
    }
}
