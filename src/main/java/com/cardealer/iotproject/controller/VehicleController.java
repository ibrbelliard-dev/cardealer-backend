package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.config.AppConfig;
import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.DashboardStats;
import com.cardealer.iotproject.model.dto.SaleRequest;
import com.cardealer.iotproject.model.dto.VehicleSearchCriteria;
import com.cardealer.iotproject.model.entity.Vehicle;
import com.cardealer.iotproject.model.enums.VehicleStatus;
import com.cardealer.iotproject.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vehicles")
@Tag(name = "Vehicle Management", description = "Endpoints for vehicle CRUD operations")
public class VehicleController {
    
    private final VehicleService vehicleService;
    private final AppConfig appConfig;
    
    public VehicleController(VehicleService vehicleService, AppConfig appConfig) {
        this.vehicleService = vehicleService;
        this.appConfig = appConfig;
    }

    /**
     * Get all vehicles
     */
    @GetMapping
    @Operation(summary = "Get all vehicles")
    public ResponseEntity<ApiResponse> getAllVehicles() {
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            return ResponseEntity.ok(ApiResponse.success("Vehículos recuperados", vehicles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al cargar vehículos: " + e.getMessage()));
        }
    }

    /**
     * Register a new vehicle
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new vehicle")
    public ResponseEntity<ApiResponse> registerVehicle(@Valid @RequestBody Vehicle vehicle) {
        try {
            Vehicle saved = vehicleService.registerVehicle(vehicle);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehículo registrado exitosamente", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al registrar vehículo: " + e.getMessage()));
        }
    }
    
    /**
     * Update vehicle information
     */
    @PutMapping("/{vehicleId}")
    @Operation(summary = "Update vehicle information")
    public ResponseEntity<ApiResponse> updateVehicle(
            @PathVariable Long vehicleId,
            @Valid @RequestBody Vehicle vehicle) {
        try {
            Vehicle updated = vehicleService.updateVehicle(vehicleId, vehicle);
            return ResponseEntity.ok(ApiResponse.success("Vehículo actualizado exitosamente", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Error al actualizar vehículo: " + e.getMessage()));
        }
    }
    
    /**
     * Mark vehicle as sold
     */
    @PostMapping("/{vehicleId}/sell")
    @Operation(summary = "Mark vehicle as sold")
    public ResponseEntity<ApiResponse> sellVehicle(
            @PathVariable Long vehicleId,
            @Valid @RequestBody SaleRequest saleRequest) {
        try {
            Vehicle sold = vehicleService.sellVehicle(vehicleId, saleRequest);
            return ResponseEntity.ok(ApiResponse.success("Vehículo vendido exitosamente", sold));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al vender vehículo: " + e.getMessage()));
        }
    }
    
    /**
     * Search vehicles in inventory
     */
    @GetMapping("/search")
    @Operation(summary = "Search vehicles in inventory")
    public ResponseEntity<ApiResponse> searchVehicles(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer yearMin,
            @RequestParam(required = false) Integer yearMax,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        VehicleSearchCriteria criteria = new VehicleSearchCriteria();
        criteria.setMake(make);
        criteria.setModel(model);
        criteria.setYearMin(yearMin);
        criteria.setYearMax(yearMax);
        criteria.setStatus(status);
        criteria.setColor(color);
        if (priceMin != null) criteria.setPriceMin(java.math.BigDecimal.valueOf(priceMin));
        if (priceMax != null) criteria.setPriceMax(java.math.BigDecimal.valueOf(priceMax));
        
        Page<Vehicle> vehicles = vehicleService.searchVehicles(criteria, 
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateAdded")));
        
        return ResponseEntity.ok(ApiResponse.success("Vehículos recuperados", vehicles));
    }
    
    /**
     * Get vehicle by ID
     */
    @GetMapping("/{vehicleId}")
    @Operation(summary = "Get vehicle by ID")
    public ResponseEntity<ApiResponse> getVehicle(@PathVariable Long vehicleId) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            
            if (vehicle.getMake() != null) {
                Hibernate.initialize(vehicle.getMake());
            }
            if (vehicle.getModel() != null) {
                Hibernate.initialize(vehicle.getModel());
            }
            
            return ResponseEntity.ok(ApiResponse.success("Vehículo recuperado", vehicle));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Vehículo no encontrado: " + e.getMessage()));
        }
    }
    
    /**
     * Get vehicle by VIN
     */
    @GetMapping("/vin/{vin}")
    @Operation(summary = "Get vehicle by VIN")
    public ResponseEntity<ApiResponse> getVehicleByVin(@PathVariable String vin) {
        try {
            Vehicle vehicle = vehicleService.getVehicleByVin(vin);
            
            if (vehicle.getMake() != null) {
                Hibernate.initialize(vehicle.getMake());
            }
            if (vehicle.getModel() != null) {
                Hibernate.initialize(vehicle.getModel());
            }
            
            return ResponseEntity.ok(ApiResponse.success("Vehículo recuperado", vehicle));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Vehículo no encontrado: " + e.getMessage()));
        }
    }
    
    /**
     * Get inventory statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get inventory statistics")
    public ResponseEntity<ApiResponse> getInventoryStats() {
        DashboardStats stats = vehicleService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Estadísticas recuperadas", stats));
    }
    
    /**
     * Get vehicles by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get vehicles by status")
    public ResponseEntity<ApiResponse> getVehiclesByStatus(@PathVariable VehicleStatus status) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Vehículos recuperados", vehicles));
    }
    
    /**
     * Get complete vehicle details with recalls, TSBs, and complaints
     */
    @GetMapping("/{vehicleId}/full")
    @Operation(summary = "Get complete vehicle details with recalls, TSBs, and complaints")
    public ResponseEntity<ApiResponse> getCompleteVehicleDetails(@PathVariable Long vehicleId) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            
            if (vehicle.getMake() != null) {
                Hibernate.initialize(vehicle.getMake());
            }
            if (vehicle.getModel() != null) {
                Hibernate.initialize(vehicle.getModel());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("vehicle", vehicle);
            response.put("recalls", Collections.emptyList());
            response.put("tsbs", Collections.emptyList());
            response.put("complaints", Collections.emptyList());
            response.put("summary", Map.of(
                "totalRecalls", 0,
                "openRecalls", 0,
                "totalTsbs", 0,
                "totalComplaints", 0
            ));
            
            return ResponseEntity.ok(ApiResponse.success("Detalles del vehículo recuperados", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al cargar detalles: " + e.getMessage()));
        }
    }
    
    /**
     * Get delete information for a vehicle (dependencies)
     */
    @GetMapping("/{vehicleId}/delete-info")
    @Operation(summary = "Get information about vehicle dependencies before deletion")
    public ResponseEntity<ApiResponse> getDeleteInfo(@PathVariable Long vehicleId) {
        try {
            Map<String, Object> deleteInfo = vehicleService.getDeleteInfo(vehicleId);
            return ResponseEntity.ok(ApiResponse.success("Información de dependencias obtenida", deleteInfo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Error al obtener información: " + e.getMessage()));
        }
    }
    
    /**
     * Delete a vehicle permanently
     */
    @DeleteMapping("/{vehicleId}")
    @Operation(summary = "Delete a vehicle permanently")
    public ResponseEntity<ApiResponse> deleteVehicle(@PathVariable Long vehicleId) {
        try {
            vehicleService.permanentlyDeleteVehicle(vehicleId);
            return ResponseEntity.ok(ApiResponse.success("Vehículo eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al eliminar el vehículo: " + e.getMessage()));
        }
    }
    
    /**
     * Soft delete a vehicle (deactivate)
     */
    @PatchMapping("/{vehicleId}/deactivate")
    @Operation(summary = "Soft delete a vehicle (deactivate)")
    public ResponseEntity<ApiResponse> deactivateVehicle(@PathVariable Long vehicleId) {
        try {
            vehicleService.softDeleteVehicle(vehicleId);
            return ResponseEntity.ok(ApiResponse.success("Vehículo desactivado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Error al desactivar vehículo: " + e.getMessage()));
        }
    }
    
    /**
     * Reactivate a soft-deleted vehicle
     */
    @PatchMapping("/{vehicleId}/activate")
    @Operation(summary = "Reactivate a soft-deleted vehicle")
    public ResponseEntity<ApiResponse> activateVehicle(@PathVariable Long vehicleId) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            vehicle.setIsActive(true);
            vehicle.setLastModified(java.time.LocalDateTime.now());
            Vehicle updated = vehicleService.updateVehicle(vehicleId, vehicle);
            return ResponseEntity.ok(ApiResponse.success("Vehículo reactivado exitosamente", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Error al reactivar vehículo: " + e.getMessage()));
        }
    }
    
    /**
     * Calculate profit for a sold vehicle
     */
    @GetMapping("/{vehicleId}/profit")
    @Operation(summary = "Calculate profit for a sold vehicle")
    public ResponseEntity<ApiResponse> calculateProfit(@PathVariable Long vehicleId) {
        try {
            java.math.BigDecimal profit = vehicleService.calculateProfit(vehicleId);
            Double margin = vehicleService.calculateProfitMargin(vehicleId);
            
            Map<String, Object> response = Map.of(
                "profit", profit,
                "margin", margin
            );
            
            return ResponseEntity.ok(ApiResponse.success("Ganancia calculada", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al calcular ganancia: " + e.getMessage()));
        }
    }
}