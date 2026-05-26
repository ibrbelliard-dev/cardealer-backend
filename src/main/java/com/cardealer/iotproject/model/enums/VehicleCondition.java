package com.cardealer.iotproject.model.enums;



public enum VehicleCondition {
    NEW("New"),
    USED("Used"),
    CERTIFIED_PRE_OWNED("Certified Pre-Owned");
    
    private final String displayName;
    
    VehicleCondition(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
