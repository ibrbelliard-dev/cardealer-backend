package com.cardealer.iotproject.controller;

import org.springframework.http.ContentDisposition;
import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.Invoice;
import com.cardealer.iotproject.service.CommissionService;
import com.cardealer.iotproject.service.InvoiceService;
import com.cardealer.iotproject.service.PdfInvoiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cardealer.iotproject.config.AppConfig;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
@Tag(name = "Invoice Management", description = "Endpoints for fiscal invoice management")
public class InvoiceController {
    
    
    // Use constructor injection instead
    private final InvoiceService invoiceService;
    private final PdfInvoiceService pdfInvoiceService;

        // Agregar esta inyección
    @Autowired
    private CommissionService commissionService;

    private final AppConfig appConfig;  // ← Agregar este campo

    
    // Constructor - this is the only place you should declare these fields
    public InvoiceController(InvoiceService invoiceService, PdfInvoiceService pdfInvoiceService,AppConfig appConfig) {
        this.invoiceService = invoiceService;
        this.pdfInvoiceService = pdfInvoiceService;
        this.appConfig= appConfig;
    }



    @PostMapping("/sale")
    @Operation(summary = "Create a vehicle sale invoice")
    public ResponseEntity<ApiResponse> createSaleInvoice(@RequestBody Map<String, Object> request) {
    try {
        // Extract parameters from the request body
        Long vehicleId = null;
        Long clientId = null;
        Long salesRepId = null;
        BigDecimal sellingPrice = null;
        String paymentMethod = null;
        String customerType = null;
        String customerRnc = null;
        String customerCedula = null;
        String invoiceDate = null;
        
        // Safely extract values
        if (request.get("vehicleId") != null) {
            vehicleId = Long.valueOf(request.get("vehicleId").toString());
        }
        if (request.get("clientId") != null) {
            clientId = Long.valueOf(request.get("clientId").toString());
        }
        if (request.get("salesRepId") != null) {
            salesRepId = Long.valueOf(request.get("salesRepId").toString());
        }
        if (request.get("sellingPrice") != null) {
            sellingPrice = new BigDecimal(request.get("sellingPrice").toString());
        }
        if (request.get("paymentMethod") != null) {
            paymentMethod = request.get("paymentMethod").toString();
        }
        if (request.get("customerType") != null) {
            customerType = request.get("customerType").toString();
        }
        if (request.get("customerRnc") != null) {
            customerRnc = request.get("customerRnc").toString();
        }
        if (request.get("customerCedula") != null) {
            customerCedula = request.get("customerCedula").toString();
        }
        if (request.get("invoiceDate") != null) {
            invoiceDate = request.get("invoiceDate").toString();
        }
        
        // Validate required parameters
        if (vehicleId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("El ID del vehículo es requerido"));
        }
        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("El ID del cliente es requerido"));
        }
        if (sellingPrice == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("El precio de venta es requerido"));
        }
        
        Invoice invoice;
        
        if (invoiceDate != null && !invoiceDate.isEmpty()) {
            // Parse the date
            LocalDateTime dateTime = LocalDateTime.parse(invoiceDate + "T00:00:00");
            invoice = invoiceService.createVehicleSaleInvoiceWithDate(
                vehicleId, clientId, salesRepId, sellingPrice, paymentMethod,
                customerType, customerRnc, customerCedula, dateTime
            );
        } else {
            invoice = invoiceService.createVehicleSaleInvoice(
                vehicleId, clientId, salesRepId, sellingPrice, paymentMethod,
                customerType, customerRnc, customerCedula
            );
        }
        
        // ========== NUEVO: Crear la comisión automáticamente ==========
        if (salesRepId != null && invoice != null && invoice.getId() != null) {
            try {
                commissionService.createCommission(
                    salesRepId,
                    vehicleId,
                    invoice.getId(),
                    sellingPrice
                );
                System.out.println("Comisión creada exitosamente para la factura: " + invoice.getId());
            } catch (Exception e) {
                // Log error but don't fail the invoice creation
                System.err.println("Error al crear comisión: " + e.getMessage());
                e.printStackTrace();
                // You can still return success for invoice but log the commission error
            }
        }
        // ==============================================================
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Factura creada exitosamente", invoice));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Error al crear factura: " + e.getMessage()));
    }
}
    
    @GetMapping
    @Operation(summary = "Get all invoices")
    public ResponseEntity<ApiResponse> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        Page<Invoice> invoices;
        if (search != null && !search.isEmpty()) {
            invoices = invoiceService.searchInvoices(search, 
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "invoiceDateTime")));
        } else {
            invoices = invoiceService.getAllInvoices(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "invoiceDateTime")));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Facturas recuperadas", invoices));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponse> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success("Factura recuperada", invoice));
    }
    
    @GetMapping("/ncf/{enNcf}")
    @Operation(summary = "Get invoice by e-NCF number")
    public ResponseEntity<ApiResponse> getInvoiceByEnNcf(@PathVariable String enNcf) {
        Invoice invoice = invoiceService.getInvoiceByEnNcf(enNcf);
        return ResponseEntity.ok(ApiResponse.success("Factura recuperada", invoice));
    }
    
    @PatchMapping("/{id}/pay")
    @Operation(summary = "Mark invoice as paid")
    public ResponseEntity<ApiResponse> markAsPaid(@PathVariable Long id) {
        try {
            Invoice invoice = invoiceService.markAsPaid(id);
            return ResponseEntity.ok(ApiResponse.success("Factura marcada como pagada", invoice));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al marcar factura: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel invoice")
    public ResponseEntity<ApiResponse> cancelInvoice(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam String cancelledBy) {
        
        try {
            Invoice invoice = invoiceService.cancelInvoice(id, reason, cancelledBy);
            return ResponseEntity.ok(ApiResponse.success("Factura cancelada", invoice));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al cancelar factura: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get invoice statistics")
    public ResponseEntity<ApiResponse> getInvoiceStats() {
        Map<String, Object> stats = invoiceService.getInvoiceStatistics();
        return ResponseEntity.ok(ApiResponse.success("Estadísticas de facturas", stats));
    }
    
    @GetMapping("/by-date")
    @Operation(summary = "Get invoices by date range")
    public ResponseEntity<ApiResponse> getInvoicesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);
        
        var invoices = invoiceService.getInvoicesByDateRange(start, end);
        return ResponseEntity.ok(ApiResponse.success("Facturas recuperadas", invoices));
    }
    
    @GetMapping("/{id}/pdf")
    @Operation(summary = "Download invoice as PDF")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        try {
            byte[] pdfBytes = pdfInvoiceService.generateInvoicePdf(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "factura_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/print")
    @Operation(summary = "View invoice as PDF in browser")
    public ResponseEntity<byte[]> viewInvoicePdf(@PathVariable Long id) {
        try {
            byte[] pdfBytes = pdfInvoiceService.generateInvoicePdf(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Correct way to set inline disposition
            headers.setContentDisposition(ContentDisposition.inline().filename("factura_" + id + ".pdf").build());
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}