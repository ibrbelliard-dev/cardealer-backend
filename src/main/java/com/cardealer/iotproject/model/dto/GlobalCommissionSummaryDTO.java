package com.cardealer.iotproject.model.dto;

import java.math.BigDecimal;
import java.util.List;

public class GlobalCommissionSummaryDTO {
    private BigDecimal totalPendingCommissions;
    private BigDecimal totalPaidCommissions;
    private BigDecimal totalCancelledCommissions;
    private BigDecimal globalTotalCommissions;
    private Long totalPendingCount;
    private Long totalPaidCount;
    private Long totalCancelledCount;
    private List<CommissionSummaryDTO> salesRepSummaries;
    
    public GlobalCommissionSummaryDTO() {}
    
    // Getters y Setters
    public BigDecimal getTotalPendingCommissions() {
        return totalPendingCommissions;
    }
    
    public void setTotalPendingCommissions(BigDecimal totalPendingCommissions) {
        this.totalPendingCommissions = totalPendingCommissions;
    }
    
    public BigDecimal getTotalPaidCommissions() {
        return totalPaidCommissions;
    }
    
    public void setTotalPaidCommissions(BigDecimal totalPaidCommissions) {
        this.totalPaidCommissions = totalPaidCommissions;
    }
    
    public BigDecimal getTotalCancelledCommissions() {
        return totalCancelledCommissions;
    }
    
    public void setTotalCancelledCommissions(BigDecimal totalCancelledCommissions) {
        this.totalCancelledCommissions = totalCancelledCommissions;
    }
    
    public BigDecimal getGlobalTotalCommissions() {
        return globalTotalCommissions;
    }
    
    public void setGlobalTotalCommissions(BigDecimal globalTotalCommissions) {
        this.globalTotalCommissions = globalTotalCommissions;
    }
    
    public Long getTotalPendingCount() {
        return totalPendingCount;
    }
    
    public void setTotalPendingCount(Long totalPendingCount) {
        this.totalPendingCount = totalPendingCount;
    }
    
    public Long getTotalPaidCount() {
        return totalPaidCount;
    }
    
    public void setTotalPaidCount(Long totalPaidCount) {
        this.totalPaidCount = totalPaidCount;
    }
    
    public Long getTotalCancelledCount() {
        return totalCancelledCount;
    }
    
    public void setTotalCancelledCount(Long totalCancelledCount) {
        this.totalCancelledCount = totalCancelledCount;
    }
    
    public List<CommissionSummaryDTO> getSalesRepSummaries() {
        return salesRepSummaries;
    }
    
    public void setSalesRepSummaries(List<CommissionSummaryDTO> salesRepSummaries) {
        this.salesRepSummaries = salesRepSummaries;
    }
}