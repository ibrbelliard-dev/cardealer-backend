package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.DashboardStats;
import com.cardealer.iotproject.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics and analytics")
// @CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    private final AppConfig appConfig;  // ← AGREGAR
    
    // ← AGREGAR constructor
    public DashboardController(DashboardService dashboardService, AppConfig appConfig) {
        this.dashboardService = dashboardService;
        this.appConfig = appConfig;
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        DashboardStats stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved", stats));
    }
    
    @GetMapping("/recent-activity")
    @Operation(summary = "Get recent activity")
    public ResponseEntity<ApiResponse> getRecentActivity(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> activities = dashboardService.getRecentActivity(limit);
        return ResponseEntity.ok(ApiResponse.success("Recent activity retrieved", activities));
    }
    
    @GetMapping("/monthly-sales")
    @Operation(summary = "Get monthly sales data")
    public ResponseEntity<ApiResponse> getMonthlySalesData() {
        List<Map<String, Object>> salesData = dashboardService.getMonthlySalesData();
        return ResponseEntity.ok(ApiResponse.success("Monthly sales data retrieved", salesData));
    }
    
    @GetMapping("/top-makes")
    @Operation(summary = "Get top selling makes")
    public ResponseEntity<ApiResponse> getTopSellingMakes() {
        Map<String, Object> topMakes = dashboardService.getTopSellingMakes();
        return ResponseEntity.ok(ApiResponse.success("Top selling makes retrieved", topMakes));
    }
    
    @GetMapping("/recent-sales")
    @Operation(summary = "Get recent sales")
    public ResponseEntity<ApiResponse> getRecentSales() {
        Map<String, Object> recentSales = dashboardService.getRecentSalesData();
        return ResponseEntity.ok(ApiResponse.success("Recent sales retrieved", recentSales));
    }
}