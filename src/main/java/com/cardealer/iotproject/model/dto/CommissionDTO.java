package com.cardealer.iotproject.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommissionDTO {
    private Long id;
    private Long salesRepId;
    private String salesRepName;
    private String salesRepCedula;
    private Long vehicleId;
    private String vehicleDescription;
    private String vin;
    private Long invoiceId;
    private BigDecimal salePrice;
    private BigDecimal commissionPercentage;
    private BigDecimal commissionAmount;
    private String status;
    private LocalDateTime paymentDate;
    private String paymentReference;
    private String notes;
    private LocalDateTime createdAt;
    private String paidBy;
    
    public CommissionDTO() {}
    
    // Getters
    public Long getId() { return id; }
    public Long getSalesRepId() { return salesRepId; }
    public String getSalesRepName() { return salesRepName; }
    public String getSalesRepCedula() { return salesRepCedula; }
    public Long getVehicleId() { return vehicleId; }
    public String getVehicleDescription() { return vehicleDescription; }
    public String getVin() { return vin; }
    public Long getInvoiceId() { return invoiceId; }
    public BigDecimal getSalePrice() { return salePrice; }
    public BigDecimal getCommissionPercentage() { return commissionPercentage; }
    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public String getStatus() { return status; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getPaymentReference() { return paymentReference; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getPaidBy() { return paidBy; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setSalesRepId(Long salesRepId) { this.salesRepId = salesRepId; }
    public void setSalesRepName(String salesRepName) { this.salesRepName = salesRepName; }
    public void setSalesRepCedula(String salesRepCedula) { this.salesRepCedula = salesRepCedula; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public void setVehicleDescription(String vehicleDescription) { this.vehicleDescription = vehicleDescription; }
    public void setVin(String vin) { this.vin = vin; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    public void setCommissionPercentage(BigDecimal commissionPercentage) { this.commissionPercentage = commissionPercentage; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }
}