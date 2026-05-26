package com.cardealer.iotproject.model.dto;

import com.cardealer.iotproject.model.enums.VehicleStatus;
import java.math.BigDecimal;

public class VehicleSearchCriteria {
    
    private String make;
    private String model;
    private Integer yearMin;
    private Integer yearMax;
    private VehicleStatus status;
    private String searchTerm;
    private String vin;
    private String color;
    private Integer mileageMin;
    private Integer mileageMax;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String condition;
    private String fuelType;
    private String transmission;
    private String driveType;
    private Integer engineCylinders;
    private String bodyClass;
    private String plantCountry;
    private Boolean isActive;
    private String sortBy;
    private String sortDirection;
    
    public VehicleSearchCriteria() {}
    
    // Getters and Setters
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYearMin() { return yearMin; }
    public void setYearMin(Integer yearMin) { this.yearMin = yearMin; }
    
    public Integer getYearMax() { return yearMax; }
    public void setYearMax(Integer yearMax) { this.yearMax = yearMax; }
    
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
    
    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Integer getMileageMin() { return mileageMin; }
    public void setMileageMin(Integer mileageMin) { this.mileageMin = mileageMin; }
    
    public Integer getMileageMax() { return mileageMax; }
    public void setMileageMax(Integer mileageMax) { this.mileageMax = mileageMax; }
    
    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }
    
    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    
    public String getDriveType() { return driveType; }
    public void setDriveType(String driveType) { this.driveType = driveType; }
    
    public Integer getEngineCylinders() { return engineCylinders; }
    public void setEngineCylinders(Integer engineCylinders) { this.engineCylinders = engineCylinders; }
    
    public String getBodyClass() { return bodyClass; }
    public void setBodyClass(String bodyClass) { this.bodyClass = bodyClass; }
    
    public String getPlantCountry() { return plantCountry; }
    public void setPlantCountry(String plantCountry) { this.plantCountry = plantCountry; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}