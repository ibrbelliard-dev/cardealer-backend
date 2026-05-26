package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/sync")
@Tag(name = "Data Sync", description = "NHTSA data synchronization endpoints")
// @CrossOrigin(origins = "http://localhost:3000")
public class SyncController {
    
    @Autowired
    private SyncService syncService;
    
    private final AppConfig appConfig;  // ← AGREGAR
    
    // ← AGREGAR constructor
    public SyncController(SyncService syncService, AppConfig appConfig) {
        this.syncService = syncService;
        this.appConfig = appConfig;
    }


    @PostMapping("/all")
    @Operation(summary = "Trigger full data sync from NHTSA")
    public ResponseEntity<ApiResponse> syncAllData() {
        CompletableFuture<Integer> result = syncService.syncAllData();
        return ResponseEntity.ok(ApiResponse.success("Full sync initiated", 
            Map.of("message", "Sync is running in background")));
    }
    
    @PostMapping("/tsbs")
    @Operation(summary = "Sync TSB/Manufacturer Communications data")
    public ResponseEntity<ApiResponse> syncTsbs() {
        int count = syncService.syncTsbData();
        return ResponseEntity.ok(ApiResponse.success("Synced " + count + " TSB records", Map.of("count", count)));
    }
    
    @GetMapping("/status")
    @Operation(summary = "Get last sync status")
    public ResponseEntity<ApiResponse> getSyncStatus() {
        // This would typically return the last sync log entry
        return ResponseEntity.ok(ApiResponse.success("Sync status retrieved", 
            Map.of("status", "idle", "lastSync", "Not available")));
    }
}