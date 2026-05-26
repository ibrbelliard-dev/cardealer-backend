package com.cardealer.iotproject.model.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private Long id;
    private String receiptNumber;
    private Long invoiceId;
    private String invoiceNcf;
    private BigDecimal invoiceTotal;
    private BigDecimal amountPaid;
    private BigDecimal remainingBalance;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String referenceNumber;
    private String paymentType;
    private String otherReason;
    private String invoiceStatus;
    private String status;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    
    // Getters and Setters (all fields)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    
    public String getInvoiceNcf() { return invoiceNcf; }
    public void setInvoiceNcf(String invoiceNcf) { this.invoiceNcf = invoiceNcf; }
    
    public BigDecimal getInvoiceTotal() { return invoiceTotal; }
    public void setInvoiceTotal(BigDecimal invoiceTotal) { this.invoiceTotal = invoiceTotal; }
    
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    
    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    
    public String getOtherReason() { return otherReason; }
    public void setOtherReason(String otherReason) { this.otherReason = otherReason; }
    
    public String getInvoiceStatus() { return invoiceStatus; }
    public void setInvoiceStatus(String invoiceStatus) { this.invoiceStatus = invoiceStatus; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}