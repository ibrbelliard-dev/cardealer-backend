package com.cardealer.iotproject.model.dto;

import java.time.LocalDateTime;

public class BranchDTO {
    private Long branchId;
    private String branchName;
    private String managerName;
    private String managerCell;
    private String managerEmail;
    private String profileImagePath;
    private String address;
    private String city;
    private String provincia;
    private String zipCode;
    private String phone;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public BranchDTO() {}
    
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