package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.PaymentRequest;
import com.cardealer.iotproject.model.dto.PaymentResponse;
import com.cardealer.iotproject.service.PaymentReceiptPdfService;
import com.cardealer.iotproject.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment Management", description = "Endpoints for payment processing")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PaymentReceiptPdfService paymentReceiptPdfService;
    
    @PostMapping("/process")
    @Operation(summary = "Process a payment")
    public ResponseEntity<ApiResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.processPayment(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pago procesado exitosamente", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al procesar pago: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all payments")
    public ResponseEntity<ApiResponse> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        Page<PaymentResponse> payments;
        if (search != null && !search.isEmpty()) {
            payments = paymentService.searchPayments(search, 
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate")));
        } else {
            payments = paymentService.getAllPayments(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate")));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Pagos recuperados", payments));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse> getPaymentById(@PathVariable Long id) {
        try {
            PaymentResponse payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(ApiResponse.success("Pago recuperado", payment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/invoice/{invoiceId}")
    @Operation(summary = "Get payments by invoice")
    public ResponseEntity<ApiResponse> getPaymentsByInvoice(
            @PathVariable Long invoiceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<PaymentResponse> payments = paymentService.getPaymentsByInvoice(invoiceId,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate")));
        
        return ResponseEntity.ok(ApiResponse.success("Pagos recuperados", payments));
    }
    
    @GetMapping("/invoice/{invoiceId}/balance")
    @Operation(summary = "Get remaining balance for invoice")
    public ResponseEntity<ApiResponse> getRemainingBalance(@PathVariable Long invoiceId) {
        try {
            BigDecimal balance = paymentService.getRemainingBalance(invoiceId);
            return ResponseEntity.ok(ApiResponse.success("Balance pendiente", balance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/summary")
    @Operation(summary = "Get payment summary statistics")
    public ResponseEntity<ApiResponse> getPaymentSummary() {
        PaymentService.PaymentSummary summary = paymentService.getPaymentSummary();
        return ResponseEntity.ok(ApiResponse.success("Resumen de pagos", summary));
    }
    
    @GetMapping("/{id}/receipt")
    @Operation(summary = "Download payment receipt as PDF")
    public ResponseEntity<byte[]> downloadPaymentReceipt(@PathVariable Long id) {
        try {
            byte[] pdfBytes = paymentReceiptPdfService.generatePaymentReceipt(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "recibo_pago_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/print")
    @Operation(summary = "View payment receipt as PDF in browser")
    public ResponseEntity<byte[]> viewPaymentReceipt(@PathVariable Long id) {
        try {
            byte[] pdfBytes = paymentReceiptPdfService.generatePaymentReceipt(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(org.springframework.http.ContentDisposition.inline()
                .filename("recibo_pago_" + id + ".pdf").build());
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
