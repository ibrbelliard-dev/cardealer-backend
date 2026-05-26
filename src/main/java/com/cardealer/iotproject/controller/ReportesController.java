// src/main/java/com/cardealer/iotproject/accounting/controller/ReportesController.java
package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.service.ReporteContableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounting/reports")
public class ReportesController {

    @Autowired
    private ReporteContableService reporteContableService;

    @GetMapping("/balance-general")
    public ResponseEntity<ApiResponse> getBalanceGeneral(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Map<String, Object> balance = reporteContableService.getBalanceGeneral(fecha);
        return ResponseEntity.ok(ApiResponse.success("Balance general generado exitosamente", balance));
    }

    @GetMapping("/estado-resultados")
    public ResponseEntity<ApiResponse> getEstadoResultados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        Map<String, Object> resultados = reporteContableService.getEstadoResultados(fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success("Estado de resultados generado exitosamente", resultados));
    }

    @GetMapping("/libro-mayor")
    public ResponseEntity<ApiResponse> getLibroMayor(
            @RequestParam String cuentaCodigo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Map<String, Object>> movimientos = reporteContableService.getLibroMayor(cuentaCodigo, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success("Libro mayor generado exitosamente", movimientos));
    }
    
}