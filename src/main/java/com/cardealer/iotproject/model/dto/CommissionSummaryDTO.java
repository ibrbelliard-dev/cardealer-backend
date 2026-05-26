package com.cardealer.iotproject.model.dto;

import java.math.BigDecimal;

public class CommissionSummaryDTO {
    private Long salesRepId;
    private String salesRepName;
    private String salesRepCedula;
    private BigDecimal commissionPercentage;
    private BigDecimal pendingTotal;
    private BigDecimal paidTotal;
    private BigDecimal cancelledTotal;
    private BigDecimal totalCommission;
    private Long pendingCount;
    private Long paidCount;
    private Long cancelledCount;
    private BigDecimal averageCommission;
    
    public CommissionSummaryDTO() {}
    
    // Getters y Setters
    public Long getSalesRepId() {
        return salesRepId;
    }
    
    public void setSalesRepId(Long salesRepId) {
        this.salesRepId = salesRepId;
    }
    
    public String getSalesRepName() {
        return salesRepName;
    }
    
    public void setSalesRepName(String salesRepName) {
        this.salesRepName = salesRepName;
    }
    
    public String getSalesRepCedula() {
        return salesRepCedula;
    }
    
    public void setSalesRepCedula(String salesRepCedula) {
        this.salesRepCedula = salesRepCedula;
    }
    
    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }
    
    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }
    
    public BigDecimal getPendingTotal() {
        return pendingTotal;
    }
    
    public void setPendingTotal(BigDecimal pendingTotal) {
        this.pendingTotal = pendingTotal;
    }
    
    public BigDecimal getPaidTotal() {
        return paidTotal;
    }
    
    public void setPaidTotal(BigDecimal paidTotal) {
        this.paidTotal = paidTotal;
    }
    
    public BigDecimal getCancelledTotal() {
        return cancelledTotal;
    }
    
    public void setCancelledTotal(BigDecimal cancelledTotal) {
        this.cancelledTotal = cancelledTotal;
    }
    
    public BigDecimal getTotalCommission() {
        return totalCommission;
    }
    
    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }
    
    public Long getPendingCount() {
        return pendingCount;
    }
    
    public void setPendingCount(Long pendingCount) {
        this.pendingCount = pendingCount;
    }
    
    public Long getPaidCount() {
        return paidCount;
    }
    
    public void setPaidCount(Long paidCount) {
        this.paidCount = paidCount;
    }
    
    public Long getCancelledCount() {
        return cancelledCount;
    }
    
    public void setCancelledCount(Long cancelledCount) {
        this.cancelledCount = cancelledCount;
    }
    
    public BigDecimal getAverageCommission() {
        return averageCommission;
    }
    
    public void setAverageCommission(BigDecimal averageCommission) {
        this.averageCommission = averageCommission;
    }
}