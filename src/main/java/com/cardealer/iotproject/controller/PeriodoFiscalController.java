package com.cardealer.iotproject.controller;
import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.PeriodoFiscal;
import com.cardealer.iotproject.service.PeriodoFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounting/periodos-fiscales")
public class PeriodoFiscalController {

    @Autowired
    private PeriodoFiscalService periodoFiscalService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        List<PeriodoFiscal> periodos = periodoFiscalService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Períodos obtenidos exitosamente", periodos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Integer id) {
        PeriodoFiscal periodo = periodoFiscalService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Período obtenido exitosamente", periodo));
    }

    @GetMapping("/abierto")
    public ResponseEntity<ApiResponse> getPeriodoAbierto() {
        PeriodoFiscal periodo = periodoFiscalService.findPeriodoAbierto();
        return ResponseEntity.ok(ApiResponse.success("Período abierto obtenido exitosamente", periodo));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody PeriodoFiscal periodoFiscal) {
        System.out.println("📥 Creando período fiscal: " + periodoFiscal);
        PeriodoFiscal nuevoPeriodo = periodoFiscalService.create(periodoFiscal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Período creado exitosamente", nuevoPeriodo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody PeriodoFiscal periodoFiscal) {
        System.out.println("📥 Actualizando período fiscal ID: " + id + ", data: " + periodoFiscal);
        PeriodoFiscal periodoActualizado = periodoFiscalService.update(id, periodoFiscal);
        return ResponseEntity.ok(ApiResponse.success("Período actualizado exitosamente", periodoActualizado));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<ApiResponse> cerrarPeriodo(@PathVariable Integer id) {
        System.out.println("🔒 Cerrando período fiscal ID: " + id);
        periodoFiscalService.cerrarPeriodo(id);
        return ResponseEntity.ok(ApiResponse.success("Período cerrado exitosamente", null));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse> activarPeriodo(@PathVariable Integer id) {
        System.out.println("✅ Activando período fiscal ID: " + id);
        periodoFiscalService.activarPeriodo(id);
        return ResponseEntity.ok(ApiResponse.success("Período activado exitosamente", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        System.out.println("🗑️ Eliminando período fiscal ID: " + id);
        try {
            periodoFiscalService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Período eliminado exitosamente", null));
        } catch (Exception e) {
            System.err.println("❌ Error eliminando período: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error eliminando el período: " + e.getMessage()));
        }
    }
}