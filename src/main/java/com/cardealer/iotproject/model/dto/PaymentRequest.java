package com.cardealer.iotproject.model.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentRequest {
    
    @NotNull(message = "El ID de la factura es requerido")
    private Long invoiceId;
    
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @DecimalMax(value = "9999999.99", message = "El monto excede el límite permitido")
    private BigDecimal amount;
    
    @NotBlank(message = "El método de pago es requerido")
    private String paymentMethod;
    
    private String referenceNumber;
    
    @NotBlank(message = "El tipo de pago es requerido")
    private String paymentType; // VEHICLE_PURCHASE, SERVICE, OTHER
    
    private String otherReason;
    
    private String notes;
    
    private String createdBy;
    
    private LocalDateTime paymentDate;
    
    // Getters and Setters
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    
    public String getOtherReason() { return otherReason; }
    public void setOtherReason(String otherReason) { this.otherReason = otherReason; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}
