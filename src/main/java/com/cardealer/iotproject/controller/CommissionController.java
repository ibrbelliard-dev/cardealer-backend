package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.*;
import com.cardealer.iotproject.model.entity.Commission;
import com.cardealer.iotproject.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commissions")
public class CommissionController {
    
    @Autowired
    private CommissionService commissionService;
    
    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        try {
            CommissionDashboardDTO stats = commissionService.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas exitosamente", stats));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCommission(@PathVariable Long id) {
        try {
            CommissionDTO commission = commissionService.getCommissionById(id);
            return ResponseEntity.ok(ApiResponse.success("Comisión encontrada", commission));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/list")
    public ResponseEntity<ApiResponse> listCommissions(@RequestBody CommissionFilterRequest filter) {
        try {
            Page<CommissionDTO> commissions = commissionService.listCommissions(filter);
            return ResponseEntity.ok(ApiResponse.success("Comisiones listadas exitosamente", commissions));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al listar comisiones: " + e.getMessage()));
        }
    }
    
    @PostMapping("/pay")
    public ResponseEntity<ApiResponse> payCommission(@RequestBody CommissionPaymentRequest request) {
        try {
            Commission commission = commissionService.payCommission(request);
            return ResponseEntity.ok(ApiResponse.success("Comisión pagada exitosamente", commission));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelCommission(@PathVariable Long id, @RequestBody CancelCommissionRequest request) {
        try {
            Commission commission = commissionService.cancelCommission(id, request.getReason());
            return ResponseEntity.ok(ApiResponse.success("Comisión cancelada exitosamente", commission));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}