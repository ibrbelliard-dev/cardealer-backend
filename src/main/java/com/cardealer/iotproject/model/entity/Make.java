package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhtsa_makes")
public class Make {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "make_id")
    private Integer makeId;
    
    @Column(name = "make_name", nullable = false, length = 150, unique = true)
    private String makeName;
    
    @Column(name = "make_display_name", length = 150)
    private String makeDisplayName;
    
    @Column(name = "make_country", length = 100)
    private String makeCountry;
    
    @Column(name = "vehicle_types", columnDefinition = "JSON")
    private String vehicleTypes;
    
    @Column(name = "last_sync_date")
    private LocalDateTime lastSyncDate;
    
    public Make() {}
    
    @PreUpdate
    protected void onUpdate() {
        lastSyncDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getMakeId() { return makeId; }
    public void setMakeId(Integer makeId) { this.makeId = makeId; }
    
    public String getMakeName() { return makeName; }
    public void setMakeName(String makeName) { this.makeName = makeName; }
    
    public String getMakeDisplayName() { return makeDisplayName; }
    public void setMakeDisplayName(String makeDisplayName) { this.makeDisplayName = makeDisplayName; }
    
    public String getMakeCountry() { return makeCountry; }
    public void setMakeCountry(String makeCountry) { this.makeCountry = makeCountry; }
    
    public String getVehicleTypes() { return vehicleTypes; }
    public void setVehicleTypes(String vehicleTypes) { this.vehicleTypes = vehicleTypes; }
    
    public LocalDateTime getLastSyncDate() { return lastSyncDate; }
    public void setLastSyncDate(LocalDateTime lastSyncDate) { this.lastSyncDate = lastSyncDate; }
}