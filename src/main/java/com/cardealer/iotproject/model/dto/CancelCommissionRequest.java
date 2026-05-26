package com.cardealer.iotproject.model.dto;

public class CancelCommissionRequest {
    private String reason;
    
    public CancelCommissionRequest() {}
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}