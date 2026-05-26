package com.cardealer.iotproject.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VinDecodeRequest {
    
    @NotBlank(message = "VIN is required")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Invalid VIN format")
    private String vin;
    
    private Integer modelYear;
    
    // Default constructor
    public VinDecodeRequest() {
    }
    
    // Constructor with fields
    public VinDecodeRequest(String vin, Integer modelYear) {
        this.vin = vin;
        this.modelYear = modelYear;
    }
    
    // Getters and Setters
    public String getVin() {
        return vin;
    }
    
    public void setVin(String vin) {
        this.vin = vin;
    }
    
    public Integer getModelYear() {
        return modelYear;
    }
    
    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }
}