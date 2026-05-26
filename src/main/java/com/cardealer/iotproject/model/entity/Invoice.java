package com.cardealer.iotproject.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_en_ncf", columnList = "en_ncf"),
    @Index(name = "idx_customer_rnc", columnList = "customer_rnc"),
    @Index(name = "idx_invoice_date", columnList = "invoice_date"),
    @Index(name = "idx_status", columnList = "status")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    // Fiscal Information - DGII Required Fields
    @Column(name = "en_ncf", unique = true, nullable = false, length = 19)
    private String enNcf; // e.g., E310000000005
    
    @Column(name = "ncf_type", nullable = false, length = 2)
    private String ncfType; // 31, 32, 33, 34
    
    @Column(name = "invoice_sequence_id")
    private Long invoiceSequenceId;
    
    // Customer Information
    @Column(name = "customer_type", nullable = false, length = 20)
    private String customerType; // "BUSINESS", "INDIVIDUAL", "FOREIGN"
    
    @Column(name = "customer_rnc", length = 11)
    private String customerRnc; // RNC para empresas
    
    @Column(name = "customer_cedula", length = 11)
    private String customerCedula; // Cédula para individuos
    
    @Column(name = "customer_pasaporte", length = 20)
    private String customerPasaporte; // Pasaporte para extranjeros
    
    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;
    
    @Column(name = "customer_email", length = 150)
    private String customerEmail;
    
    @Column(name = "customer_phone", length = 20)
    private String customerPhone;
    
    @Column(name = "customer_address", length = 500)
    private String customerAddress;
    
    // Invoice Details
    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDateTime;
    
    @Column(name = "invoice_type", length = 20)
    private String invoiceType; // "SALE", "CREDIT_NOTE", "DEBIT_NOTE"
    
    // Financial Breakdown (ITBIS 18%)
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "itbis_rate", precision = 5, scale = 2)
    private BigDecimal itbisRate = new BigDecimal("18.00");
    
    @Column(name = "itbis_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal itbisAmount;
    
    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    
    @Column(name = "itbis_status", length = 20)
    private String itbisStatus; // "GRAVADO" (taxable), "EXENTO" (exempt), "EXCLUIDO"
    
    // Payment Information
    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // "CASH", "CREDIT_CARD", "BANK_TRANSFER", "FINANCING"
    
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;
    
    // Vehicle Sales Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @JsonIgnoreProperties({"invoices", "hibernateLazyInitializer", "handler"})
    private Vehicle vehicle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"invoices", "hibernateLazyInitializer", "handler"})
    private Client client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_rep_id")
    @JsonIgnoreProperties({"invoices", "hibernateLazyInitializer", "handler"})
    private SalesRep salesRep;
    
    // Status
    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, PAID, CANCELLED, VOID
    
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;
    
    // Metadata
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference  // This side is the "parent" side
    private List<InvoiceItem> items = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (invoiceDateTime == null) invoiceDateTime = LocalDateTime.now();
        if (itbisRate == null) itbisRate = new BigDecimal("18.00");
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (itbisAmount == null) itbisAmount = BigDecimal.ZERO;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public void calculateTotals() {
        if (this.subtotal != null && this.itbisRate != null) {
            this.itbisAmount = this.subtotal.multiply(this.itbisRate).divide(new BigDecimal("100"));
            this.total = this.subtotal.add(this.itbisAmount).subtract(this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO);
        }
    }
    
    // Getters and Setters (all your existing getters/setters)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEnNcf() { return enNcf; }
    public void setEnNcf(String enNcf) { this.enNcf = enNcf; }
    
    public String getNcfType() { return ncfType; }
    public void setNcfType(String ncfType) { this.ncfType = ncfType; }
    
    public Long getInvoiceSequenceId() { return invoiceSequenceId; }
    public void setInvoiceSequenceId(Long invoiceSequenceId) { this.invoiceSequenceId = invoiceSequenceId; }
    
    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }
    
    public String getCustomerRnc() { return customerRnc; }
    public void setCustomerRnc(String customerRnc) { this.customerRnc = customerRnc; }
    
    public String getCustomerCedula() { return customerCedula; }
    public void setCustomerCedula(String customerCedula) { this.customerCedula = customerCedula; }
    
    public String getCustomerPasaporte() { return customerPasaporte; }
    public void setCustomerPasaporte(String customerPasaporte) { this.customerPasaporte = customerPasaporte; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    
    public LocalDateTime getInvoiceDateTime() { return invoiceDateTime; }
    public void setInvoiceDateTime(LocalDateTime invoiceDateTime) { this.invoiceDateTime = invoiceDateTime; }
    
    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getItbisRate() { return itbisRate; }
    public void setItbisRate(BigDecimal itbisRate) { this.itbisRate = itbisRate; }
    
    public BigDecimal getItbisAmount() { return itbisAmount; }
    public void setItbisAmount(BigDecimal itbisAmount) { this.itbisAmount = itbisAmount; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public String getItbisStatus() { return itbisStatus; }
    public void setItbisStatus(String itbisStatus) { this.itbisStatus = itbisStatus; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public SalesRep getSalesRep() { return salesRep; }
    public void setSalesRep(SalesRep salesRep) { this.salesRep = salesRep; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    
    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
}