// src/main/java/com/cardealer/iotproject/model/dto/RolDTO.java
package com.cardealer.iotproject.model.dto;

import java.util.Set;

public class RolDTO {
    private String roleTitle;
    private String description;
    private Boolean isActive;
    private Set<Long> permisoIds;
    
    // Constructor vacío
    public RolDTO() {}
    
    // Getters y Setters
    public String getRoleTitle() {
        return roleTitle;
    }
    
    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Set<Long> getPermisoIds() {
        return permisoIds;
    }
    
    public void setPermisoIds(Set<Long> permisoIds) {
        this.permisoIds = permisoIds;
    }
}