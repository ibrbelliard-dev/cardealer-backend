package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhtsa_models")
public class Model {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Integer modelId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "make_id", nullable = false)
    private Make make;
    
    @Column(name = "model_name", nullable = false, length = 150)
    private String modelName;
    
    @Column(name = "model_display_name", length = 200)
    private String modelDisplayName;
    
    @Column(name = "vehicle_type", length = 100)
    private String vehicleType;
    
    @Column(name = "model_year_from")
    private Integer modelYearFrom;
    
    @Column(name = "model_year_to")
    private Integer modelYearTo;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public Model() {}
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }
    
    // Getters and Setters
    public Integer getModelId() { return modelId; }
    public void setModelId(Integer modelId) { this.modelId = modelId; }
    
    public Make getMake() { return make; }
    public void setMake(Make make) { this.make = make; }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    public String getModelDisplayName() { return modelDisplayName; }
    public void setModelDisplayName(String modelDisplayName) { this.modelDisplayName = modelDisplayName; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public Integer getModelYearFrom() { return modelYearFrom; }
    public void setModelYearFrom(Integer modelYearFrom) { this.modelYearFrom = modelYearFrom; }
    
    public Integer getModelYearTo() { return modelYearTo; }
    public void setModelYearTo(Integer modelYearTo) { this.modelYearTo = modelYearTo; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}