package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.Recall;
import com.cardealer.iotproject.service.RecallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recalls")
@Tag(name = "Recalls", description = "Vehicle recall management endpoints")
@CrossOrigin(origins = "http://localhost:3000")
public class RecallController {
    
    @Autowired
    private RecallService recallService;
    
    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get all recalls for a specific vehicle")
    public ResponseEntity<ApiResponse> getRecallsForVehicle(@PathVariable Long vehicleId) {
        List<Recall> recalls = recallService.getRecallsForVehicle(vehicleId);
        return ResponseEntity.ok(ApiResponse.success("Recalls retrieved", recalls));
    }
    
    @GetMapping("/campaign/{campaignNumber}")
    @Operation(summary = "Get recall by campaign number")
    public ResponseEntity<ApiResponse> getRecallByCampaign(@PathVariable String campaignNumber) {
        Recall recall = recallService.getRecallByCampaignNumber(campaignNumber);
        return ResponseEntity.ok(ApiResponse.success("Recall retrieved", recall));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search recalls")
    public ResponseEntity<ApiResponse> searchRecalls(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Recall> recalls = recallService.searchRecalls(make, model, year, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Recalls retrieved", recalls));
    }
    
    @GetMapping("/component/{component}")
    @Operation(summary = "Get recalls by component")
    public ResponseEntity<ApiResponse> getRecallsByComponent(@PathVariable String component) {
        List<Recall> recalls = recallService.getRecallsByComponent(component);
        return ResponseEntity.ok(ApiResponse.success("Recalls retrieved", recalls));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get recall statistics")
    public ResponseEntity<ApiResponse> getRecallStatistics() {
        List<Object[]> statsByComponent = recallService.getRecallStatsByComponent();
        List<Object[]> statsByYear = recallService.getRecallStatsByYear();
        
        return ResponseEntity.ok(ApiResponse.success("Recall statistics", 
            Map.of("byComponent", statsByComponent, "byYear", statsByYear)));
    }



}