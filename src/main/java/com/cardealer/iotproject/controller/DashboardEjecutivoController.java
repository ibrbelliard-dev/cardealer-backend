// src/main/java/com/cardealer/iotproject/controller/DashboardEjecutivoController.java
package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.service.DashboardEjecutivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard/ejecutivo")
public class DashboardEjecutivoController {

    @Autowired
    private DashboardEjecutivoService dashboardEjecutivoService;

    @GetMapping
    public ResponseEntity<ApiResponse> getDashboardEjecutivo() {
        Map<String, Object> dashboard = dashboardEjecutivoService.getDashboardEjecutivo();
        return ResponseEntity.ok(ApiResponse.success("Dashboard ejecutivo obtenido exitosamente", dashboard));
    }
}