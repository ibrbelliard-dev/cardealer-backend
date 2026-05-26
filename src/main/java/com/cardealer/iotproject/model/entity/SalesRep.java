package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_reps", indexes = {
    @Index(name = "idx_cedula", columnList = "cedula"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status")
})
public class SalesRep {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "cedula", unique = true, length = 20)
    private String cedula;
    
    @Column(name = "email", unique = true, length = 150)
    private String email;
    
    @Column(name = "cell_phone", length = 20)
    private String cellPhone;
    
    @Column(name = "business_phone", length = 20)
    private String businessPhone;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "commission_percentage", precision = 5, scale = 2)
    private BigDecimal commissionPercentage = BigDecimal.ZERO;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Column(name = "termination_date")
    private LocalDate terminationDate;
    
    @Column(name = "status")
    private Integer status = 1; // 1 = Active, 0 = Inactive
    
    @Column(name = "sales_quota")
    private BigDecimal salesQuota;
    
    @Column(name = "total_sales")
    private BigDecimal totalSales = BigDecimal.ZERO;

    
     @Column(name = "total_commissions_paid")
    private BigDecimal totalCommissionsPaid = BigDecimal.ZERO;

    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public SalesRep() {}
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = 1;
        if (commissionPercentage == null) commissionPercentage = BigDecimal.ZERO;
        if (totalSales == null) totalSales = BigDecimal.ZERO;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCellPhone() { return cellPhone; }
    public void setCellPhone(String cellPhone) { this.cellPhone = cellPhone; }
    
    public String getBusinessPhone() { return businessPhone; }
    public void setBusinessPhone(String businessPhone) { this.businessPhone = businessPhone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public BigDecimal getCommissionPercentage() { return commissionPercentage; }
    public void setCommissionPercentage(BigDecimal commissionPercentage) { this.commissionPercentage = commissionPercentage; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    public LocalDate getTerminationDate() { return terminationDate; }
    public void setTerminationDate(LocalDate terminationDate) { this.terminationDate = terminationDate; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public BigDecimal getSalesQuota() { return salesQuota; }
    public void setSalesQuota(BigDecimal salesQuota) { this.salesQuota = salesQuota; }
    
    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    

    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getStatusLabel() {
        return status != null && status == 1 ? "Activo" : "Inactivo";
    }
    
    public boolean isActive() {
        return status != null && status == 1;
    }

    public BigDecimal getTotalCommissionsPaid() {
        return totalCommissionsPaid;
    }

    public void setTotalCommissionsPaid(BigDecimal totalCommissionsPaid) {
        this.totalCommissionsPaid = totalCommissionsPaid;
    }
}