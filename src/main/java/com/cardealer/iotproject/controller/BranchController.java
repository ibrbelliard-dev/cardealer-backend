package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.BranchDTO;
import com.cardealer.iotproject.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/branches")  // ← CAMBIADO: quitamos /api porque ya está en context-path
// @CrossOrigin(origins = "*")
public class BranchController {
    
    @Autowired
    private BranchService branchService;
    
    /**
     * Get all branches
     * GET /api/branches (el /api viene del context-path)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBranches() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<BranchDTO> branches = branchService.getAllBranches();
            response.put("success", true);
            response.put("data", branches);
            response.put("total", branches.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get only active branches
     * GET /api/branches/active
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveBranches() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<BranchDTO> branches = branchService.getActiveBranches();
            response.put("success", true);
            response.put("data", branches);
            response.put("total", branches.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get branch by ID
     * GET /api/branches/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBranchById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            BranchDTO branch = branchService.getBranchById(id);
            response.put("success", true);
            response.put("data", branch);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Create a new branch
     * POST /api/branches
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBranch(@RequestBody BranchDTO branchDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            BranchDTO createdBranch = branchService.createBranch(branchDTO);
            response.put("success", true);
            response.put("data", createdBranch);
            response.put("message", "Sucursal creada exitosamente");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Update an existing branch
     * PUT /api/branches/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBranch(
            @PathVariable Long id,
            @RequestBody BranchDTO branchDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            BranchDTO updatedBranch = branchService.updateBranch(id, branchDTO);
            response.put("success", true);
            response.put("data", updatedBranch);
            response.put("message", "Sucursal actualizada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Delete a branch
     * DELETE /api/branches/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBranch(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            branchService.deleteBranch(id);
            response.put("success", true);
            response.put("message", "Sucursal eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Toggle branch active status
     * PATCH /api/branches/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleBranchStatus(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            BranchDTO branch = branchService.toggleBranchStatus(id);
            response.put("success", true);
            response.put("data", branch);
            response.put("message", branch.getIsActive() ? 
                "Sucursal activada exitosamente" : "Sucursal desactivada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get branch statistics
     * GET /api/branches/statistics/summary
     */
    @GetMapping("/statistics/summary")
    public ResponseEntity<Map<String, Object>> getBranchStatistics() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalBranches", branchService.getTotalBranchCount());
            stats.put("activeBranches", branchService.getActiveBranchCount());
            stats.put("inactiveBranches", branchService.getTotalBranchCount() - branchService.getActiveBranchCount());
            
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}