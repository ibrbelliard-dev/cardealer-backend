package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_sequences")
public class InvoiceSequence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "ncf_type", nullable = false, length = 2)
    private String ncfType; // 31, 32, 33, 34, etc.
    
    @Column(name = "sequence_type", nullable = false, length = 20)
    private String sequenceType; // "ELECTRONIC" or "PHYSICAL"
    
    @Column(name = "prefix", length = 2)
    private String prefix; // "E" for electronic
    
    @Column(name = "current_number", nullable = false)
    private Long currentNumber = 0L;
    
    @Column(name = "start_range")
    private Long startRange;
    
    @Column(name = "end_range")
    private Long endRange;
    
    @Column(name = "authorization_number", length = 50)
    private String authorizationNumber;
    
    @Column(name = "valid_from")
    private LocalDateTime validFrom;
    
    @Column(name = "valid_until")
    private LocalDateTime validUntil;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currentNumber == null) currentNumber = 0L;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNcfType() { return ncfType; }
    public void setNcfType(String ncfType) { this.ncfType = ncfType; }
    
    public String getSequenceType() { return sequenceType; }
    public void setSequenceType(String sequenceType) { this.sequenceType = sequenceType; }
    
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    
    public Long getCurrentNumber() { return currentNumber; }
    public void setCurrentNumber(Long currentNumber) { this.currentNumber = currentNumber; }
    
    public Long getStartRange() { return startRange; }
    public void setStartRange(Long startRange) { this.startRange = startRange; }
    
    public Long getEndRange() { return endRange; }
    public void setEndRange(Long endRange) { this.endRange = endRange; }
    
    public String getAuthorizationNumber() { return authorizationNumber; }
    public void setAuthorizationNumber(String authorizationNumber) { this.authorizationNumber = authorizationNumber; }
    
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    
    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper method to generate next e-NCF
    public String generateNextENcf() {
        this.currentNumber++;
        String numberStr = String.format("%010d", this.currentNumber);
        return this.prefix + this.ncfType + numberStr;
    }
}