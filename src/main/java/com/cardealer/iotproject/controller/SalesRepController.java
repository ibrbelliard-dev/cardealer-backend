package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.SalesRep;
import com.cardealer.iotproject.service.SalesRepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sales-reps")
@Tag(name = "Sales Representatives", description = "Endpoints for sales rep management")
@CrossOrigin(origins = "http://localhost:8085")
public class SalesRepController {
    
    @Autowired
    private SalesRepService salesRepService;
    
private final AppConfig appConfig;  // ← AGREGAR
    
    // ← AGREGAR constructor
    public SalesRepController(SalesRepService salesRepService, AppConfig appConfig) {
        this.salesRepService = salesRepService;
        this.appConfig = appConfig;
    }

    
    @GetMapping
    @Operation(summary = "Get all sales representatives")
    public ResponseEntity<ApiResponse> getAllSalesReps(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        Page<SalesRep> reps;
        if (search != null && !search.isEmpty()) {
            reps = salesRepService.searchSalesReps(search, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        } else {
            reps = salesRepService.getAllSalesReps(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Vendedores recuperados", reps));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active sales representatives")
    public ResponseEntity<ApiResponse> getActiveSalesReps() {
        List<SalesRep> reps = salesRepService.getActiveSalesReps();
        return ResponseEntity.ok(ApiResponse.success("Vendedores activos recuperados", reps));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get sales representative by ID")
    public ResponseEntity<ApiResponse> getSalesRepById(@PathVariable Long id) {
        SalesRep rep = salesRepService.getSalesRepById(id);
        return ResponseEntity.ok(ApiResponse.success("Vendedor recuperado", rep));
    }
    
    @PostMapping
    @Operation(summary = "Create a new sales representative")
    public ResponseEntity<ApiResponse> createSalesRep(@Valid @RequestBody SalesRep salesRep) {
        try {
            SalesRep created = salesRepService.createSalesRep(salesRep);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vendedor creado exitosamente", created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al crear vendedor: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update sales representative")
    public ResponseEntity<ApiResponse> updateSalesRep(@PathVariable Long id, @Valid @RequestBody SalesRep salesRep) {
        try {
            SalesRep updated = salesRepService.updateSalesRep(id, salesRep);
            return ResponseEntity.ok(ApiResponse.success("Vendedor actualizado exitosamente", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al actualizar vendedor: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update sales representative status")
    public ResponseEntity<ApiResponse> updateSalesRepStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            SalesRep updated = salesRepService.updateSalesRepStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Estado del vendedor actualizado", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al actualizar estado: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sales representative")
    public ResponseEntity<ApiResponse> deleteSalesRep(@PathVariable Long id) {
        try {
            salesRepService.deleteSalesRep(id);
            return ResponseEntity.ok(ApiResponse.success("Vendedor eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al eliminar vendedor: " + e.getMessage()));
        }
    }
    
    @GetMapping("/top-performers")
    @Operation(summary = "Get top performing sales representatives")
    public ResponseEntity<ApiResponse> getTopPerformers(@RequestParam(defaultValue = "5") int limit) {
        List<SalesRep> topPerformers = salesRepService.getTopPerformers(limit);
        return ResponseEntity.ok(ApiResponse.success("Mejores vendedores recuperados", topPerformers));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get sales representative statistics")
    public ResponseEntity<ApiResponse> getSalesRepStats() {
        List<SalesRep> allReps = salesRepService.getAllSalesReps();
        long totalReps = allReps.size();
        long activeReps = allReps.stream().filter(r -> r.getStatus() == 1).count();
        long inactiveReps = totalReps - activeReps;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReps", totalReps);
        stats.put("activeReps", activeReps);
        stats.put("inactiveReps", inactiveReps);
        
        return ResponseEntity.ok(ApiResponse.success("Estadísticas de vendedores", stats));
    }
}