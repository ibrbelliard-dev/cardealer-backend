package com.cardealer.iotproject.model.dto;

import com.cardealer.iotproject.model.enums.VehicleStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VehicleDTO {
    private Long vehicleId;
    private String vin;
    private String makeName;
    private String modelName;
    private Integer modelYear;
    private String color;
    private String interiorColor;
    private Integer mileage;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private VehicleStatus status;
    private String condition;
    private String bodyClass;
    private String driveType;
    private Integer engineCylinders;
    private BigDecimal engineDisplacementL;
    private String fuelTypePrimary;
    private String transmissionStyle;
    private Integer doors;
    private LocalDateTime dateAdded;
    private String mileageUnit; // "KM" o "MILLAS"
    
    // Constructors
    public VehicleDTO() {}

// Agrega getter y setter:
public String getMileageUnit() { return mileageUnit; }
public void setMileageUnit(String mileageUnit) { this.mileageUnit = mileageUnit; }
    
    // Getters and Setters
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    
    public String getMakeName() { return makeName; }
    public void setMakeName(String makeName) { this.makeName = makeName; }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    public Integer getModelYear() { return modelYear; }
    public void setModelYear(Integer modelYear) { this.modelYear = modelYear; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getInteriorColor() { return interiorColor; }
    public void setInteriorColor(String interiorColor) { this.interiorColor = interiorColor; }
    
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getBodyClass() { return bodyClass; }
    public void setBodyClass(String bodyClass) { this.bodyClass = bodyClass; }
    
    public String getDriveType() { return driveType; }
    public void setDriveType(String driveType) { this.driveType = driveType; }
    
    public Integer getEngineCylinders() { return engineCylinders; }
    public void setEngineCylinders(Integer engineCylinders) { this.engineCylinders = engineCylinders; }
    
    public BigDecimal getEngineDisplacementL() { return engineDisplacementL; }
    public void setEngineDisplacementL(BigDecimal engineDisplacementL) { this.engineDisplacementL = engineDisplacementL; }
    
    public String getFuelTypePrimary() { return fuelTypePrimary; }
    public void setFuelTypePrimary(String fuelTypePrimary) { this.fuelTypePrimary = fuelTypePrimary; }
    
    public String getTransmissionStyle() { return transmissionStyle; }
    public void setTransmissionStyle(String transmissionStyle) { this.transmissionStyle = transmissionStyle; }
    
    public Integer getDoors() { return doors; }
    public void setDoors(Integer doors) { this.doors = doors; }
    
    public LocalDateTime getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
}