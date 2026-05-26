
// Request para pagar comisión
package com.cardealer.iotproject.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommissionPaymentRequest {
    private Long commissionId;
    private String paymentReference;
    private String paidBy;
    private String notes;
    
    // Getters and Setters
    public Long getCommissionId() { return commissionId; }
    public void setCommissionId(Long commissionId) { this.commissionId = commissionId; }
    
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    
    public String getPaidBy() { return paidBy; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}