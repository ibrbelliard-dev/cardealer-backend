// src/main/java/com/cardealer/iotproject/accounting/controller/AsientoContableController.java
package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.AsientoContable;
import com.cardealer.iotproject.model.entity.DetalleAsiento;
import com.cardealer.iotproject.service.AsientoContableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounting/asientos")
public class AsientoContableController {

    @Autowired
    private AsientoContableService asientoService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        List<AsientoContable> asientos = asientoService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Asientos obtenidos exitosamente", asientos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Integer id) {
        Map<String, Object> asiento = asientoService.getAsientoWithDetalles(id);
        return ResponseEntity.ok(ApiResponse.success("Asiento obtenido exitosamente", asiento));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Map<String, Object> request) {
        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(java.time.LocalDate.parse((String) request.get("fecha")));
        asiento.setDescripcion((String) request.get("descripcion"));
        asiento.setTipoAsiento((String) request.get("tipoAsiento"));
        asiento.setTotalDebe(((Number) request.get("totalDebe")).doubleValue());
        asiento.setTotalHaber(((Number) request.get("totalHaber")).doubleValue());
        
        List<DetalleAsiento> detalles = (List<DetalleAsiento>) request.get("detalles");
        
        AsientoContable saved = asientoService.create(asiento, detalles);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Asiento creado exitosamente", saved));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<ApiResponse> aprobar(@PathVariable Integer id) {
        asientoService.aprobar(id);
        return ResponseEntity.ok(ApiResponse.success("Asiento aprobado exitosamente", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        asientoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Asiento eliminado exitosamente", null));
    }


    // src/main/java/com/cardealer/iotproject/accounting/controller/AsientoContableController.java
// Agrega este método

@PutMapping("/{id}")
public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
    try {
        AsientoContable asientoActualizado = asientoService.updateWithDetails(id, request);
        return ResponseEntity.ok(ApiResponse.success("Asiento actualizado exitosamente", asientoActualizado));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }
}
}