package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vin_decode_cache")
public class VinDecodeCache {
    
    @Id
    @Column(name = "vin", length = 17)
    private String vin;
    
    @Column(name = "decoded_json", nullable = false, columnDefinition = "TEXT")
    private String decodedData;
    
    @Column(name = "last_decoded")
    private LocalDateTime lastDecoded;
    
    @Column(name = "decode_count")
    private Integer decodeCount = 1;
    
    public VinDecodeCache() {}
    
    @PrePersist
    protected void onCreate() {
        lastDecoded = LocalDateTime.now();
        if (decodeCount == null) decodeCount = 1;
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastDecoded = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    
    public String getDecodedData() { return decodedData; }
    public void setDecodedData(String decodedData) { this.decodedData = decodedData; }
    
    public LocalDateTime getLastDecoded() { return lastDecoded; }
    public void setLastDecoded(LocalDateTime lastDecoded) { this.lastDecoded = lastDecoded; }
    
    public Integer getDecodeCount() { return decodeCount; }
    public void setDecodeCount(Integer decodeCount) { this.decodeCount = decodeCount; }
}