package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.dto.PaymentRequest;
import com.cardealer.iotproject.model.dto.PaymentResponse;
import com.cardealer.iotproject.model.entity.Invoice;
import com.cardealer.iotproject.model.entity.Payment;
import com.cardealer.iotproject.repository.InvoiceRepository;
import com.cardealer.iotproject.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    private static final DateTimeFormatter RECEIPT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // Validate invoice exists
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + request.getInvoiceId()));
        
        // Validate invoice is not cancelled
        if ("CANCELLED".equals(invoice.getStatus()) || "VOID".equals(invoice.getStatus())) {
            throw new RuntimeException("No se puede procesar pago para una factura cancelada o anulada");
        }
        
        // Calculate total paid so far
        BigDecimal totalPaid = paymentRepository.getTotalPaidByInvoiceId(invoice.getId());
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        
        BigDecimal newTotalPaid = totalPaid.add(request.getAmount());
        
        // Validate payment doesn't exceed invoice total
        if (newTotalPaid.compareTo(invoice.getTotal()) > 0) {
            throw new RuntimeException("El monto del pago excede el balance pendiente de la factura");
        }
        
        // Create payment record
        Payment payment = new Payment();
        payment.setReceiptNumber(generateReceiptNumber());
        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setPaymentType(request.getPaymentType());
        payment.setOtherReason(request.getOtherReason());
        payment.setNotes(request.getNotes());
        payment.setCreatedBy(request.getCreatedBy());
        
        if (request.getPaymentDate() != null) {
            payment.setPaymentDate(request.getPaymentDate());
        }
        
        payment.setStatus("COMPLETED");
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice status based on payment
        updateInvoiceStatus(invoice);
        
        // Build response
        return buildPaymentResponse(savedPayment, invoice);
    }
    
    @Transactional
    public void updateInvoiceStatus(Invoice invoice) {
        BigDecimal totalPaid = paymentRepository.getTotalPaidByInvoiceId(invoice.getId());
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        
        if (totalPaid.compareTo(invoice.getTotal()) >= 0) {
            invoice.setStatus("PAID");
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus("PARTIALLY_PAID");
        } else {
            invoice.setStatus("PENDING");
        }
        
        invoiceRepository.save(invoice);
    }
    
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
        Invoice invoice = payment.getInvoice();
        return buildPaymentResponse(payment, invoice);
    }
    
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
            .map(payment -> buildPaymentResponse(payment, payment.getInvoice()));
    }
    
    public Page<PaymentResponse> getPaymentsByInvoice(Long invoiceId, Pageable pageable) {
        return paymentRepository.findByInvoiceId(invoiceId, pageable)
            .map(payment -> buildPaymentResponse(payment, payment.getInvoice()));
    }
    
    public Page<PaymentResponse> searchPayments(String search, Pageable pageable) {
        return paymentRepository.searchPayments(search, pageable)
            .map(payment -> buildPaymentResponse(payment, payment.getInvoice()));
    }
    
    public BigDecimal getRemainingBalance(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        BigDecimal totalPaid = paymentRepository.getTotalPaidByInvoiceId(invoiceId);
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        return invoice.getTotal().subtract(totalPaid);
    }
    
    public PaymentSummary getPaymentSummary() {
        PaymentSummary summary = new PaymentSummary();
        summary.setTotalPaymentsAmount(paymentRepository.getTotalPaymentsAmount());
        summary.setTotalPaymentsCount(paymentRepository.getTotalPaymentsCount());
        summary.setVehiclePurchaseTotal(paymentRepository.getTotalByPaymentType("VEHICLE_PURCHASE"));
        summary.setServiceTotal(paymentRepository.getTotalByPaymentType("SERVICE"));
        summary.setOtherTotal(paymentRepository.getTotalByPaymentType("OTHER"));
        return summary;
    }
    
    private String generateReceiptNumber() {
        String timestamp = LocalDateTime.now().format(RECEIPT_FORMATTER);
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return "REC-" + timestamp + "-" + random;
    }
    
    private PaymentResponse buildPaymentResponse(Payment payment, Invoice invoice) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setReceiptNumber(payment.getReceiptNumber());
        response.setInvoiceId(invoice.getId());
        response.setInvoiceNcf(invoice.getEnNcf());
        response.setInvoiceTotal(invoice.getTotal());
        
        BigDecimal totalPaid = paymentRepository.getTotalPaidByInvoiceId(invoice.getId());
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        response.setAmountPaid(totalPaid);
        response.setRemainingBalance(invoice.getTotal().subtract(totalPaid));
        
        response.setPaymentDate(payment.getPaymentDate());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setReferenceNumber(payment.getReferenceNumber());
        response.setPaymentType(payment.getPaymentType());
        response.setOtherReason(payment.getOtherReason());
        response.setInvoiceStatus(invoice.getStatus());
        response.setStatus(payment.getStatus());
        response.setNotes(payment.getNotes());
        response.setCreatedBy(payment.getCreatedBy());
        response.setCreatedAt(payment.getCreatedAt());
        
        return response;
    }
    
    // Inner class for summary
    public static class PaymentSummary {
        private BigDecimal totalPaymentsAmount;
        private Long totalPaymentsCount;
        private BigDecimal vehiclePurchaseTotal;
        private BigDecimal serviceTotal;
        private BigDecimal otherTotal;
        
        // Getters and Setters
        public BigDecimal getTotalPaymentsAmount() { return totalPaymentsAmount; }
        public void setTotalPaymentsAmount(BigDecimal totalPaymentsAmount) { this.totalPaymentsAmount = totalPaymentsAmount; }
        
        public Long getTotalPaymentsCount() { return totalPaymentsCount; }
        public void setTotalPaymentsCount(Long totalPaymentsCount) { this.totalPaymentsCount = totalPaymentsCount; }
        
        public BigDecimal getVehiclePurchaseTotal() { return vehiclePurchaseTotal; }
        public void setVehiclePurchaseTotal(BigDecimal vehiclePurchaseTotal) { this.vehiclePurchaseTotal = vehiclePurchaseTotal; }
        
        public BigDecimal getServiceTotal() { return serviceTotal; }
        public void setServiceTotal(BigDecimal serviceTotal) { this.serviceTotal = serviceTotal; }
        
        public BigDecimal getOtherTotal() { return otherTotal; }
        public void setOtherTotal(BigDecimal otherTotal) { this.otherTotal = otherTotal; }
    }
}