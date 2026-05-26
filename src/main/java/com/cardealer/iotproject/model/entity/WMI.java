package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhtsa_wmi")
public class WMI {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wmi_id")
    private Long wmiId;
    
    @Column(name = "wmi", nullable = false, unique = true, length = 3)
    private String wmi;
    
    @Column(name = "manufacturer_id")
    private Integer manufacturerId;
    
    @Column(name = "manufacturer_name", length = 200)
    private String manufacturerName;
    
    @Column(name = "vehicle_type", length = 100)
    private String vehicleType;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "plant_city", length = 100)
    private String plantCity;
    
    @Column(name = "plant_state", length = 50)
    private String plantState;
    
    @Column(name = "plant_country", length = 100)
    private String plantCountry;
    
    @Column(name = "date_created")
    private LocalDate dateCreated;
    
    @Column(name = "date_available")
    private LocalDate dateAvailable;
    
    @Column(name = "date_stopped")
    private LocalDate dateStopped;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "sync_date")
    private LocalDateTime syncDate;
    
    public WMI() {}
    
    @PrePersist
    protected void onCreate() {
        syncDate = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }
    
    // Getters and Setters
    public Long getWmiId() { return wmiId; }
    public void setWmiId(Long wmiId) { this.wmiId = wmiId; }
    
    public String getWmi() { return wmi; }
    public void setWmi(String wmi) { this.wmi = wmi; }
    
    public Integer getManufacturerId() { return manufacturerId; }
    public void setManufacturerId(Integer manufacturerId) { this.manufacturerId = manufacturerId; }
    
    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPlantCity() { return plantCity; }
    public void setPlantCity(String plantCity) { this.plantCity = plantCity; }
    
    public String getPlantState() { return plantState; }
    public void setPlantState(String plantState) { this.plantState = plantState; }
    
    public String getPlantCountry() { return plantCountry; }
    public void setPlantCountry(String plantCountry) { this.plantCountry = plantCountry; }
    
    public LocalDate getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDate dateCreated) { this.dateCreated = dateCreated; }
    
    public LocalDate getDateAvailable() { return dateAvailable; }
    public void setDateAvailable(LocalDate dateAvailable) { this.dateAvailable = dateAvailable; }
    
    public LocalDate getDateStopped() { return dateStopped; }
    public void setDateStopped(LocalDate dateStopped) { this.dateStopped = dateStopped; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getSyncDate() { return syncDate; }
    public void setSyncDate(LocalDateTime syncDate) { this.syncDate = syncDate; }
}