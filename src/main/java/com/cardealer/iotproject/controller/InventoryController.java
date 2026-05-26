package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.VehicleSearchCriteria;
import com.cardealer.iotproject.model.entity.Vehicle;
import com.cardealer.iotproject.model.enums.VehicleStatus;
import com.cardealer.iotproject.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Inventory management endpoints")
// @CrossOrigin(origins = "http://localhost:8085")
public class InventoryController {
    
    @Autowired
    private VehicleService vehicleService;
     private final AppConfig appConfig;  // ← AGREGAR
    
    // ← AGREGAR constructor
    public InventoryController(VehicleService vehicleService, AppConfig appConfig) {
        this.vehicleService = vehicleService;
        this.appConfig = appConfig;
    }
    @GetMapping
    @Operation(summary = "Get all inventory vehicles")
    public ResponseEntity<ApiResponse> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        VehicleSearchCriteria criteria = new VehicleSearchCriteria();
        criteria.setStatus(VehicleStatus.AVAILABLE);
        criteria.setIsActive(true);
        
        Page<Vehicle> vehicles = vehicleService.searchVehicles(criteria, 
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateAdded")));
        
        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved", vehicles));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get inventory statistics")
    public ResponseEntity<ApiResponse> getInventoryStats() {
        List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();
        
        long totalVehicles = availableVehicles.size();
        BigDecimal totalValue = availableVehicles.stream()
            .map(Vehicle::getPurchasePrice)
            .filter(price -> price != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averagePrice = totalVehicles > 0 ? 
            totalValue.divide(BigDecimal.valueOf(totalVehicles), 2, java.math.RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        
        Map<String, Object> stats = Map.of(
            "totalVehicles", totalVehicles,
            "totalValue", totalValue,
            "averagePrice", averagePrice
        );
        
        return ResponseEntity.ok(ApiResponse.success("Inventory statistics retrieved", stats));
    }
    
    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get vehicles by status")
    public ResponseEntity<ApiResponse> getVehiclesByStatus(@PathVariable VehicleStatus status) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Vehicles retrieved", vehicles));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search inventory")
    public ResponseEntity<ApiResponse> searchInventory(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer yearMin,
            @RequestParam(required = false) Integer yearMax,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        VehicleSearchCriteria criteria = new VehicleSearchCriteria();
        criteria.setMake(make);
        criteria.setModel(model);
        criteria.setYearMin(yearMin);
        criteria.setYearMax(yearMax);
        criteria.setColor(color);
        criteria.setPriceMin(minPrice);
        criteria.setPriceMax(maxPrice);
        criteria.setStatus(VehicleStatus.AVAILABLE);
        criteria.setIsActive(true);
        
        Page<Vehicle> vehicles = vehicleService.searchVehicles(criteria, 
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateAdded")));
        
        return ResponseEntity.ok(ApiResponse.success("Search results", vehicles));
    }
}