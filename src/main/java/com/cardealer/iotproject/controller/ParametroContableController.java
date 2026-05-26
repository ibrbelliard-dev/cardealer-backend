// src/main/java/com/cardealer/iotproject/accounting/controller/ParametroContableController.java
package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.service.ParametroContableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/accounting/parametros")
public class ParametroContableController {

    @Autowired
    private ParametroContableService parametroContableService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        Map<String, Object> parametros = parametroContableService.getAllParametros();
        return ResponseEntity.ok(ApiResponse.success("Parámetros obtenidos exitosamente", parametros));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> saveAll(@RequestBody Map<String, Object> parametros) {
        parametroContableService.saveAllParametros(parametros);
        return ResponseEntity.ok(ApiResponse.success("Parámetros guardados exitosamente", null));
    }
}