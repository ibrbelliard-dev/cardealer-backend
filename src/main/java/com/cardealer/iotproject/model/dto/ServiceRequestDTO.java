package com.cardealer.iotproject.model.dto;

import java.math.BigDecimal;

public class ServiceRequestDTO {
    private Long vehicleId;
    private String serviceType;
    private String description;
    private BigDecimal estimatedCost;
    private String mechanic;
    private Double laborHours;
    
    // Getters and Setters
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    
    public String getMechanic() { return mechanic; }
    public void setMechanic(String mechanic) { this.mechanic = mechanic; }
    
    public Double getLaborHours() { return laborHours; }
    public void setLaborHours(Double laborHours) { this.laborHours = laborHours; }
}