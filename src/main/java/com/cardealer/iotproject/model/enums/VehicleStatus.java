package com.cardealer.iotproject.model.enums;

public enum VehicleStatus {
    AVAILABLE("Available"),
    SOLD("Sold"),
    PENDING("Pending"),
    RESERVED("Reserved"),
    SERVICE("In Service");
    
    private final String displayName;
    
    VehicleStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
