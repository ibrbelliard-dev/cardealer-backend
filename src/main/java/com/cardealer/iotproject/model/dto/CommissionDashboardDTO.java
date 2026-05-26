package com.cardealer.iotproject.model.dto;

import java.math.BigDecimal;

public class CommissionDashboardDTO {
    private BigDecimal totalPending = BigDecimal.ZERO;
    private BigDecimal totalPaid = BigDecimal.ZERO;
    private Long totalPendingCount = 0L;
    private Long totalPaidCount = 0L;
    private BigDecimal monthCommissions = BigDecimal.ZERO;
    private BigDecimal monthPending = BigDecimal.ZERO;
    private BigDecimal monthPaid = BigDecimal.ZERO;
    
    public CommissionDashboardDTO() {}
    
    public BigDecimal getTotalPending() { return totalPending; }
    public void setTotalPending(BigDecimal totalPending) { this.totalPending = totalPending; }
    
    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    
    public Long getTotalPendingCount() { return totalPendingCount; }
    public void setTotalPendingCount(Long totalPendingCount) { this.totalPendingCount = totalPendingCount; }
    
    public Long getTotalPaidCount() { return totalPaidCount; }
    public void setTotalPaidCount(Long totalPaidCount) { this.totalPaidCount = totalPaidCount; }
    
    public BigDecimal getMonthCommissions() { return monthCommissions; }
    public void setMonthCommissions(BigDecimal monthCommissions) { this.monthCommissions = monthCommissions; }
    
    public BigDecimal getMonthPending() { return monthPending; }
    public void setMonthPending(BigDecimal monthPending) { this.monthPending = monthPending; }
    
    public BigDecimal getMonthPaid() { return monthPaid; }
    public void setMonthPaid(BigDecimal monthPaid) { this.monthPaid = monthPaid; }
}