package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.ServiceRequest;
import com.cardealer.iotproject.repository.ServiceRequestRepository;
import com.cardealer.iotproject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ServiceRequestService {
    
    private static final Logger log = Logger.getLogger(ServiceRequestService.class.getName());
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Transactional(readOnly = true)
    public List<ServiceRequest> getAllServiceRequests() {
        return serviceRequestRepository.findAllWithVehicle();
    }
    
    @Transactional(readOnly = true)
    public Page<ServiceRequest> getAllServiceRequests(Pageable pageable) {
        return serviceRequestRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public ServiceRequest getServiceRequestById(Long id) {
        return serviceRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud de servicio no encontrada con ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<ServiceRequest> getServiceRequestsByVehicle(Long vehicleId) {
        return serviceRequestRepository.findByVehicleVehicleId(vehicleId);
    }
    
    @Transactional
    public ServiceRequest createServiceRequest(ServiceRequest serviceRequest) {
        if (serviceRequest.getVehicle() == null || serviceRequest.getVehicle().getVehicleId() == null) {
            throw new RuntimeException("El vehículo es requerido");
        }
        
        vehicleRepository.findById(serviceRequest.getVehicle().getVehicleId())
            .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        
        serviceRequest.setStatus("PENDING");
        serviceRequest.setServiceDate(LocalDateTime.now());
        
        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);
        log.info("Solicitud de servicio creada: ID " + saved.getId());
        return saved;
    }
    
    @Transactional
    public ServiceRequest updateServiceRequest(Long id, ServiceRequest serviceRequestDetails) {
        ServiceRequest serviceRequest = getServiceRequestById(id);
        
        if (serviceRequestDetails.getServiceType() != null) {
            serviceRequest.setServiceType(serviceRequestDetails.getServiceType());
        }
        if (serviceRequestDetails.getDescription() != null) {
            serviceRequest.setDescription(serviceRequestDetails.getDescription());
        }
        if (serviceRequestDetails.getEstimatedCost() != null) {
            serviceRequest.setEstimatedCost(serviceRequestDetails.getEstimatedCost());
        }
        if (serviceRequestDetails.getActualCost() != null) {
            serviceRequest.setActualCost(serviceRequestDetails.getActualCost());
        }
        if (serviceRequestDetails.getMechanic() != null) {
            serviceRequest.setMechanic(serviceRequestDetails.getMechanic());
        }
        if (serviceRequestDetails.getLaborHours() != null) {
            serviceRequest.setLaborHours(serviceRequestDetails.getLaborHours());
        }
        if (serviceRequestDetails.getNotes() != null) {
            serviceRequest.setNotes(serviceRequestDetails.getNotes());
        }
        
        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);
        log.info("Solicitud de servicio actualizada: ID " + saved.getId());
        return saved;
    }
    
    @Transactional
    public ServiceRequest updateServiceRequestStatus(Long id, String status) {
        ServiceRequest serviceRequest = getServiceRequestById(id);
        String oldStatus = serviceRequest.getStatus();
        serviceRequest.setStatus(status);
        
        if ("COMPLETED".equals(status) || "READY".equals(status)) {
            serviceRequest.setCompletedDate(LocalDateTime.now());
        }
        
        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);
        log.info("Estado actualizado: ID " + id + " de " + oldStatus + " a " + status);
        return saved;
    }
    
    @Transactional
    public void deleteServiceRequest(Long id) {
        ServiceRequest serviceRequest = getServiceRequestById(id);
        serviceRequestRepository.delete(serviceRequest);
        log.info("Solicitud eliminada: ID " + id);
    }
    
    @Transactional(readOnly = true)
    public Page<ServiceRequest> searchServiceRequests(String status, String mechanic, Pageable pageable) {
        return serviceRequestRepository.searchServiceRequests(status, mechanic, pageable);
    }
    
    @Transactional(readOnly = true)
    public long getPendingCount() {
        return serviceRequestRepository.countByStatus("PENDING");
    }
    
    @Transactional(readOnly = true)
    public long getInProgressCount() {
        return serviceRequestRepository.countByStatus("IN_PROGRESS");
    }
    
    @Transactional(readOnly = true)
    public long getCompletedCount() {
        return serviceRequestRepository.countByStatus("COMPLETED");
    }
}