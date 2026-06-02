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

     
    /**
     * Obtener todas las secuencias NCF
     */
    @GetMapping("/ncf/sequences")
    public ResponseEntity<ApiResponse> getNcfSequences() {
        Map<String, Object> sequences = parametroContableService.getNcfSequences();
        return ResponseEntity.ok(ApiResponse.success("Secuencias NCF obtenidas exitosamente", sequences));
    }
    
    /**
     * Obtener el próximo número NCF para un tipo específico
     */
    @GetMapping("/ncf/next/{tipoNCF}")
    public ResponseEntity<ApiResponse> getNextNcfNumber(@PathVariable String tipoNCF) {
        String nextNcf = parametroContableService.getNextNcfNumber(tipoNCF);
        return ResponseEntity.ok(ApiResponse.success("Próximo NCF obtenido exitosamente", nextNcf));
    }
    
    /**
     * Resetear secuencia NCF para un tipo específico
     */
    @PostMapping("/ncf/reset")
    public ResponseEntity<ApiResponse> resetNcfSequence(@RequestBody Map<String, Object> request) {
        String tipoNCF = (String) request.get("tipoNCF");
        Long newNumber = ((Number) request.get("newNumber")).longValue();
        
        parametroContableService.resetNcfSequence(tipoNCF, newNumber);
        return ResponseEntity.ok(ApiResponse.success("Secuencia NCF reseteada exitosamente", null));
    }
}