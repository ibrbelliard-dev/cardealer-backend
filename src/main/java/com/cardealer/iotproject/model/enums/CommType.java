package com.cardealer.iotproject.model.enums;


public enum CommType {
    SBR("Service Bulletin"),
    SCA("Service Campaign"),
    WPE("Warranty"),
    OTA("Over The Air"),
    EMI("Emissions"),
    OTH("Other");
    
    private final String displayName;
    
    CommType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}