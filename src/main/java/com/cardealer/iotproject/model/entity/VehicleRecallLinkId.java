package com.cardealer.iotproject.model.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VehicleRecallLinkId implements Serializable {
    
    private Long vehicleId;
    
    private Long recallId;
    
    public VehicleRecallLinkId() {}
    
    public VehicleRecallLinkId(Long vehicleId, Long recallId) {
        this.vehicleId = vehicleId;
        this.recallId = recallId;
    }
    
    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public Long getRecallId() {
        return recallId;
    }
    
    public void setRecallId(Long recallId) {
        this.recallId = recallId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleRecallLinkId that = (VehicleRecallLinkId) o;
        return Objects.equals(vehicleId, that.vehicleId) &&
               Objects.equals(recallId, that.recallId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(vehicleId, recallId);
    }
    
    @Override
    public String toString() {
        return "VehicleRecallLinkId{" +
               "vehicleId=" + vehicleId +
               ", recallId=" + recallId +
               '}';
    }
}