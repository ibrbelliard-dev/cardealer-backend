// src/main/java/com/cardealer/iotproject/accounting/controller/DashboardContableController.java
package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.service.ReporteContableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/accounting/dashboard")
public class DashboardContableController {

    @Autowired
    private ReporteContableService reporteContableService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        Map<String, Object> stats = reporteContableService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Estadísticas del dashboard obtenidas exitosamente", stats));
    }
}