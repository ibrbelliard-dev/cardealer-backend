package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.ServiceRequestDTO;
import com.cardealer.iotproject.model.entity.ServiceRequest;
import com.cardealer.iotproject.model.entity.Vehicle;
import com.cardealer.iotproject.repository.ServiceRequestRepository;
import com.cardealer.iotproject.repository.VehicleRepository;
import com.cardealer.iotproject.service.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
@RequestMapping("/service")
// @CrossOrigin(origins = "http://localhost:8085")
public class ServiceRequestController {
    
    @Autowired
    private ServiceRequestService serviceRequestService;
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;  // Add this
    
    @Autowired
    private VehicleRepository vehicleRepository;

      private final AppConfig appConfig;  // ← AGREGAR
    
    // ← AGREGAR constructor
    public ServiceRequestController(ServiceRequestService serviceRequestService,
                                     ServiceRequestRepository serviceRequestRepository,
                                     VehicleRepository vehicleRepository,
                                     AppConfig appConfig) {
        this.serviceRequestService = serviceRequestService;
        this.serviceRequestRepository = serviceRequestRepository;
        this.vehicleRepository = vehicleRepository;
        this.appConfig = appConfig;
    }
    
    @GetMapping("/requests")
    @Operation(summary = "Get all service requests")
    public ResponseEntity<ApiResponse> getAllServiceRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String mechanic) {
        
        try {
            // Use repository to fetch with vehicle eagerly loaded
            List<ServiceRequest> allRequests = serviceRequestRepository.findAllWithVehicle();
            
            // Apply filters if needed
            if (status != null && !status.isEmpty()) {
                allRequests = allRequests.stream()
                    .filter(r -> r.getStatus().equals(status))
                    .toList();
            }
            if (mechanic != null && !mechanic.isEmpty()) {
                allRequests = allRequests.stream()
                    .filter(r -> r.getMechanic() != null && r.getMechanic().toLowerCase().contains(mechanic.toLowerCase()))
                    .toList();
            }
            
            // Paginate
            int start = (int) PageRequest.of(page, size).getOffset();
            int end = Math.min((start + size), allRequests.size());
            List<ServiceRequest> pageContent = allRequests.subList(start, end);
            Page<ServiceRequest> requests = new PageImpl<>(pageContent, 
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")), 
                allRequests.size());
            
            return ResponseEntity.ok(ApiResponse.success("Solicitudes de servicio recuperadas", requests));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al recuperar solicitudes: " + e.getMessage()));
        }
    }
    
    @GetMapping("/requests/{id}")
    @Operation(summary = "Get service request by ID")
    public ResponseEntity<ApiResponse> getServiceRequestById(@PathVariable Long id) {
        try {
            ServiceRequest request = serviceRequestRepository.findByIdWithVehicle(id);
            if (request == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Solicitud no encontrada"));
            }
            return ResponseEntity.ok(ApiResponse.success("Solicitud recuperada", request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al recuperar solicitud: " + e.getMessage()));
        }
    }
    
    @GetMapping("/requests/vehicle/{vehicleId}")
    @Operation(summary = "Get service requests by vehicle")
    public ResponseEntity<ApiResponse> getServiceRequestsByVehicle(@PathVariable Long vehicleId) {
        try {
            List<ServiceRequest> requests = serviceRequestRepository.findByVehicleVehicleIdWithVehicle(vehicleId);
            return ResponseEntity.ok(ApiResponse.success("Solicitudes recuperadas", requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al recuperar solicitudes: " + e.getMessage()));
        }
    }
    
    @PostMapping("/requests")
    @Operation(summary = "Create a new service request")
    public ResponseEntity<ApiResponse> createServiceRequest(@Valid @RequestBody ServiceRequestDTO requestDTO) {
        try {
            Vehicle vehicle = vehicleRepository.findById(requestDTO.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + requestDTO.getVehicleId()));
            
            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setVehicle(vehicle);
            serviceRequest.setServiceType(requestDTO.getServiceType());
            serviceRequest.setDescription(requestDTO.getDescription());
            serviceRequest.setEstimatedCost(requestDTO.getEstimatedCost());
            serviceRequest.setMechanic(requestDTO.getMechanic());
            serviceRequest.setLaborHours(requestDTO.getLaborHours());
            
            ServiceRequest created = serviceRequestService.createServiceRequest(serviceRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Solicitud de servicio creada exitosamente", created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al crear solicitud: " + e.getMessage()));
        }
    }
    
    @PutMapping("/requests/{id}")
    @Operation(summary = "Update service request")
    public ResponseEntity<ApiResponse> updateServiceRequest(@PathVariable Long id, @Valid @RequestBody ServiceRequestDTO requestDTO) {
        try {
            ServiceRequest existingRequest = serviceRequestService.getServiceRequestById(id);
            
            if (requestDTO.getVehicleId() != null) {
                Vehicle vehicle = vehicleRepository.findById(requestDTO.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + requestDTO.getVehicleId()));
                existingRequest.setVehicle(vehicle);
            }
            if (requestDTO.getServiceType() != null) {
                existingRequest.setServiceType(requestDTO.getServiceType());
            }
            if (requestDTO.getDescription() != null) {
                existingRequest.setDescription(requestDTO.getDescription());
            }
            if (requestDTO.getEstimatedCost() != null) {
                existingRequest.setEstimatedCost(requestDTO.getEstimatedCost());
            }
            if (requestDTO.getMechanic() != null) {
                existingRequest.setMechanic(requestDTO.getMechanic());
            }
            if (requestDTO.getLaborHours() != null) {
                existingRequest.setLaborHours(requestDTO.getLaborHours());
            }
            
            ServiceRequest updated = serviceRequestService.updateServiceRequest(id, existingRequest);
            return ResponseEntity.ok(ApiResponse.success("Solicitud actualizada exitosamente", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al actualizar solicitud: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/requests/{id}/status")
    @Operation(summary = "Update service request status")
    public ResponseEntity<ApiResponse> updateServiceRequestStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            ServiceRequest updated = serviceRequestService.updateServiceRequestStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Estado actualizado exitosamente", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al actualizar estado: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/requests/{id}")
    @Operation(summary = "Delete service request")
    public ResponseEntity<ApiResponse> deleteServiceRequest(@PathVariable Long id) {
        try {
            serviceRequestService.deleteServiceRequest(id);
            return ResponseEntity.ok(ApiResponse.success("Solicitud eliminada exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al eliminar solicitud: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get service request statistics")
    public ResponseEntity<ApiResponse> getServiceStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", serviceRequestService.getPendingCount());
        stats.put("inProgress", serviceRequestService.getInProgressCount());
        stats.put("completed", serviceRequestService.getCompletedCount());
        stats.put("total", serviceRequestService.getAllServiceRequests().size());
        
        return ResponseEntity.ok(ApiResponse.success("Estadísticas recuperadas", stats));
    }
}