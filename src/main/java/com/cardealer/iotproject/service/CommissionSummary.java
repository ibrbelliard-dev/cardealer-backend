
// Clase para resumen de comisiones
package com.cardealer.iotproject.service;

import java.math.BigDecimal;

public class CommissionSummary {
    private Long salesRepId;
    private String salesRepName;
    private BigDecimal commissionPercentage;
    private BigDecimal pendingTotal;
    private BigDecimal paidTotal;
    private BigDecimal totalCommission;
    private long pendingCount;
    private long paidCount;
    
    // Getters and Setters
    public Long getSalesRepId() { return salesRepId; }
    public void setSalesRepId(Long salesRepId) { this.salesRepId = salesRepId; }
    
    public String getSalesRepName() { return salesRepName; }
    public void setSalesRepName(String salesRepName) { this.salesRepName = salesRepName; }
    
    public BigDecimal getCommissionPercentage() { return commissionPercentage; }
    public void setCommissionPercentage(BigDecimal commissionPercentage) { this.commissionPercentage = commissionPercentage; }
    
    public BigDecimal getPendingTotal() { return pendingTotal; }
    public void setPendingTotal(BigDecimal pendingTotal) { this.pendingTotal = pendingTotal; }
    
    public BigDecimal getPaidTotal() { return paidTotal; }
    public void setPaidTotal(BigDecimal paidTotal) { this.paidTotal = paidTotal; }
    
    public BigDecimal getTotalCommission() { return totalCommission; }
    public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }
    
    public long getPendingCount() { return pendingCount; }
    public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }
    
    public long getPaidCount() { return paidCount; }
    public void setPaidCount(long paidCount) { this.paidCount = paidCount; }
}