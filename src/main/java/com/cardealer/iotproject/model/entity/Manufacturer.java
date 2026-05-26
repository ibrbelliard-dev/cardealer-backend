package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhtsa_manufacturers")
public class Manufacturer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manufacturer_id")
    private Long manufacturerId;
    
    @Column(name = "nhtsa_manufacturer_id", unique = true)
    private Integer nhtsaManufacturerId;
    
    @Column(name = "manufacturer_name", nullable = false, length = 200)
    private String manufacturerName;
    
    @Column(name = "manufacturer_type", length = 100)
    private String manufacturerType;
    
    @Column(name = "address_line1", length = 255)
    private String addressLine1;
    
    @Column(name = "address_line2", length = 255)
    private String addressLine2;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "state", length = 50)
    private String state;
    
    @Column(name = "zipcode", length = 20)
    private String zipcode;
    
    @Column(name = "country", length = 100)
    private String country;
    
    @Column(name = "contact_phone", length = 50)
    private String contactPhone;
    
    @Column(name = "contact_email", length = 150)
    private String contactEmail;
    
    @Column(name = "primary_wmi", length = 3)
    private String primaryWmi;
    
    @Column(name = "date_added")
    private LocalDate dateAdded;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    public Manufacturer() {}
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateAdded == null) dateAdded = LocalDate.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getManufacturerId() { return manufacturerId; }
    public void setManufacturerId(Long manufacturerId) { this.manufacturerId = manufacturerId; }
    
    public Integer getNhtsaManufacturerId() { return nhtsaManufacturerId; }
    public void setNhtsaManufacturerId(Integer nhtsaManufacturerId) { this.nhtsaManufacturerId = nhtsaManufacturerId; }
    
    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    
    public String getManufacturerType() { return manufacturerType; }
    public void setManufacturerType(String manufacturerType) { this.manufacturerType = manufacturerType; }
    
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public String getPrimaryWmi() { return primaryWmi; }
    public void setPrimaryWmi(String primaryWmi) { this.primaryWmi = primaryWmi; }
    
    public LocalDate getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDate dateAdded) { this.dateAdded = dateAdded; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}