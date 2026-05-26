package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "branches")
public class Branch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long branchId;
    
    @Column(name = "branch_name", nullable = false, length = 200)
    private String branchName;
    
    @Column(name = "manager_name", length = 100)
    private String managerName;
    
    @Column(name = "manager_cell", length = 50)
    private String managerCell;
    
    @Column(name = "manager_email", length = 150)
    private String managerEmail;
    
    @Column(name = "profile_image_path", length = 500)
    private String profileImagePath;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "provincia", length = 100)
    private String provincia;
    
    @Column(name = "zip_code", length = 20)
    private String zipCode;
    
    @Column(name = "phone", length = 50)
    private String phone;
    
    @Column(name = "email", length = 150)
    private String email;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Branch() {}
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getBranchId() {
        return branchId;
    }
    
    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    
    public String getManagerCell() {
        return managerCell;
    }
    
    public void setManagerCell(String managerCell) {
        this.managerCell = managerCell;
    }
    
    public String getManagerEmail() {
        return managerEmail;
    }
    
    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
    
    public String getProfileImagePath() {
        return profileImagePath;
    }
    
    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getProvincia() {
        return provincia;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}