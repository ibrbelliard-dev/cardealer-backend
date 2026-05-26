package com.cardealer.iotproject.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_items")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InvoiceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonBackReference  // This breaks the recursion - this is the "child" side
    private Invoice invoice;
    
    @Column(name = "item_type", length = 50)
    private String itemType; // "VEHICLE", "SERVICE", "PART", "ACCESSORY"
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
    
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "itbis_rate", precision = 5, scale = 2)
    private BigDecimal itbisRate = new BigDecimal("18.00");
    
    @Column(name = "itbis_amount", precision = 12, scale = 2)
    private BigDecimal itbisAmount;
    
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    
    @Column(name = "itbis_status", length = 20)
    private String itbisStatus; // "GRAVADO", "EXENTO", "EXCLUIDO"
    
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;
    
    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public InvoiceItem() {}
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        calculateTotals();
    }
    
    public void calculateTotals() {
        if (this.unitPrice != null && this.quantity != null) {
            this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
            if (this.itbisRate != null) {
                this.itbisAmount = this.subtotal.multiply(this.itbisRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            } else {
                this.itbisAmount = BigDecimal.ZERO;
            }
            BigDecimal discount = this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO;
            this.total = this.subtotal.add(this.itbisAmount).subtract(discount);
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getItbisRate() { return itbisRate; }
    public void setItbisRate(BigDecimal itbisRate) { this.itbisRate = itbisRate; }
    
    public BigDecimal getItbisAmount() { return itbisAmount; }
    public void setItbisAmount(BigDecimal itbisAmount) { this.itbisAmount = itbisAmount; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public String getItbisStatus() { return itbisStatus; }
    public void setItbisStatus(String itbisStatus) { this.itbisStatus = itbisStatus; }
    
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}