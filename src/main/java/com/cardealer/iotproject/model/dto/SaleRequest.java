package com.cardealer.iotproject.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class SaleRequest {
    
    @NotNull(message = "Selling price is required")
    @Positive(message = "Selling price must be positive")
    private BigDecimal sellingPrice;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    private String customerPhone;
    private String customerEmail;
    private String notes;
    private String paymentMethod;
    private String financingInstitution;
    private BigDecimal downPayment;
    private BigDecimal tradeInValue;
    private String tradeInVin;
    private Integer warrantyMonths;
    private BigDecimal warrantyCost;
    private String salespersonName;
    private String salespersonId;
    
    public SaleRequest() {
    }
    
    public SaleRequest(BigDecimal sellingPrice, String customerName) {
        this.sellingPrice = sellingPrice;
        this.customerName = customerName;
    }
    
    // Getters
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public String getNotes() { return notes; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getFinancingInstitution() { return financingInstitution; }
    public BigDecimal getDownPayment() { return downPayment; }
    public BigDecimal getTradeInValue() { return tradeInValue; }
    public String getTradeInVin() { return tradeInVin; }
    public Integer getWarrantyMonths() { return warrantyMonths; }
    public BigDecimal getWarrantyCost() { return warrantyCost; }
    public String getSalespersonName() { return salespersonName; }
    public String getSalespersonId() { return salespersonId; }
    
    // Setters
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setFinancingInstitution(String financingInstitution) { this.financingInstitution = financingInstitution; }
    public void setDownPayment(BigDecimal downPayment) { this.downPayment = downPayment; }
    public void setTradeInValue(BigDecimal tradeInValue) { this.tradeInValue = tradeInValue; }
    public void setTradeInVin(String tradeInVin) { this.tradeInVin = tradeInVin; }
    public void setWarrantyMonths(Integer warrantyMonths) { this.warrantyMonths = warrantyMonths; }
    public void setWarrantyCost(BigDecimal warrantyCost) { this.warrantyCost = warrantyCost; }
    public void setSalespersonName(String salespersonName) { this.salespersonName = salespersonName; }
    public void setSalespersonId(String salespersonId) { this.salespersonId = salespersonId; }
}