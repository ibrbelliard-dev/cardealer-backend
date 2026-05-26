package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.VinDecodeRequest;
import com.cardealer.iotproject.service.NhtsaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/nhtsa")
@Tag(name = "NHTSA Integration", description = "Endpoints for NHTSA API integration")
// @CrossOrigin(origins = "http://localhost:3000")
public class NhtsaController {
    
    @Autowired
    private NhtsaService nhtsaService;
      private final AppConfig appConfig;  // ← AGREGAR
    
    // ← AGREGAR constructor
    public NhtsaController(NhtsaService nhtsaService, AppConfig appConfig) {
        this.nhtsaService = nhtsaService;
        this.appConfig = appConfig;
    }
    
    @GetMapping("/makes")
    @Operation(summary = "Get all vehicle makes from NHTSA")
    public ResponseEntity<ApiResponse> getAllMakes() {
        int count = nhtsaService.syncAllMakes();
        return ResponseEntity.ok(ApiResponse.success("Synced " + count + " makes", Map.of("count", count)));
    }
    
    @GetMapping("/makes/{makeName}/models")
    @Operation(summary = "Get models for a specific make")
    public ResponseEntity<ApiResponse> getModelsForMake(
            @PathVariable String makeName,
            @RequestParam(required = false) Integer year) {
        int count = nhtsaService.syncModelsForMake(makeName);
        return ResponseEntity.ok(ApiResponse.success("Synced " + count + " models", Map.of("count", count)));
    }
    
    @PostMapping("/decode-vin")
    @Operation(summary = "Decode a VIN number")
    public ResponseEntity<ApiResponse> decodeVin(@Valid @RequestBody VinDecodeRequest request) {
        Map<String, Object> decodedData = nhtsaService.decodeVin(request.getVin(), request.getModelYear());
        
        // Truncate string fields to prevent database errors when saving
        Map<String, Object> truncatedData = truncateDecodedData(decodedData);
        
        return ResponseEntity.ok(ApiResponse.success("VIN decoded successfully", truncatedData));
    }
    
    @GetMapping("/status")
    @Operation(summary = "Get NHTSA API status")
    public ResponseEntity<ApiResponse> getApiStatus() {
        Map<String, Object> status = Map.of(
            "status", "operational",
            "message", "NHTSA API integration is active"
        );
        return ResponseEntity.ok(ApiResponse.success("NHTSA API status", status));
    }
    
    /**
     * Truncates string fields in the decoded VIN data to fit database column limits
     */
    private Map<String, Object> truncateDecodedData(Map<String, Object> decodedData) {
        if (decodedData == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> truncated = new HashMap<>(decodedData);
        
        // Database column limits:
        // drive_type: VARCHAR(20)
        // body_class: VARCHAR(100)  
        // fuel_type_primary: VARCHAR(50)
        // transmission_style: VARCHAR(100)
        // plant_city: VARCHAR(100)
        // plant_country: VARCHAR(100)
        // plant_state: VARCHAR(50)
        
        truncateField(truncated, "DriveType", 20);
        truncateField(truncated, "BodyClass", 100);
        truncateField(truncated, "FuelTypePrimary", 50);
        truncateField(truncated, "TransmissionStyle", 100);
        truncateField(truncated, "PlantCity", 100);
        truncateField(truncated, "PlantCountry", 100);
        truncateField(truncated, "PlantState", 50);
        truncateField(truncated, "Make", 100);
        truncateField(truncated, "Model", 100);
        truncateField(truncated, "Manufacturer", 100);
        truncateField(truncated, "Series", 100);
        truncateField(truncated, "Trim", 100);
        truncateField(truncated, "VehicleType", 50);
        
        return truncated;
    }
    
    /**
     * Helper method to truncate a specific field in the map
     */
    private void truncateField(Map<String, Object> data, String fieldName, int maxLength) {
        if (data.containsKey(fieldName)) {
            Object value = data.get(fieldName);
            if (value instanceof String) {
                String stringValue = (String) value;
                if (stringValue != null && stringValue.length() > maxLength) {
                    data.put(fieldName, stringValue.substring(0, maxLength));
                }
            }
        }
    }
}