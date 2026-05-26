package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.*;
import com.cardealer.iotproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InvoiceService {
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private SalesRepRepository salesRepRepository;
    
    @Autowired
    private InvoiceSequenceService sequenceService;
    
    private static final String NCF_TYPE_CREDIT = "31"; // Crédito Fiscal
    private static final String NCF_TYPE_CONSUMPTION = "32"; // Consumo
    private static final BigDecimal ITBIS_RATE = new BigDecimal("18.00");
    
    @Transactional
    public Invoice createVehicleSaleInvoice(
            Long vehicleId, Long clientId, Long salesRepId,
            BigDecimal sellingPrice, String paymentMethod,
            String customerType, String customerRnc, String customerCedula) {
        
        return createVehicleSaleInvoiceWithDate(vehicleId, clientId, salesRepId, 
            sellingPrice, paymentMethod, customerType, customerRnc, customerCedula, 
            LocalDateTime.now());
    }
    
    @Transactional
    public Invoice createVehicleSaleInvoiceWithDate(
            Long vehicleId, Long clientId, Long salesRepId,
            BigDecimal sellingPrice, String paymentMethod,
            String customerType, String customerRnc, String customerCedula,
            LocalDateTime invoiceDate) {
        
        // Get entities
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
        
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));
        
        SalesRep salesRep = null;
        if (salesRepId != null) {
            salesRep = salesRepRepository.findById(salesRepId)
                .orElse(null);
        }
        
        // Create invoice
        Invoice invoice = new Invoice();
        
        // Generate NCF (e-NCF) using the sequence service
        String enNcf = sequenceService.generateNextEnNcf();
        invoice.setEnNcf(enNcf);
        
        // Set NCF type based on customer type
        if ("BUSINESS".equals(customerType) && customerRnc != null && !customerRnc.isEmpty()) {
            invoice.setNcfType(NCF_TYPE_CREDIT); // Crédito Fiscal for businesses
        } else {
            invoice.setNcfType(NCF_TYPE_CONSUMPTION); // Consumo for individuals
        }
        
        // Set customer info
        invoice.setCustomerType(customerType != null ? customerType : "INDIVIDUAL");
        invoice.setCustomerRnc(customerRnc);
        invoice.setCustomerCedula(customerCedula);
        invoice.setCustomerName(client.getFullName());
        invoice.setCustomerEmail(client.getEmailAddr());
        invoice.setCustomerPhone(client.getCell());
        invoice.setCustomerAddress(client.getAddress());
        
        // Set invoice details
        invoice.setInvoiceDateTime(invoiceDate);
        invoice.setInvoiceType("SALE");
        invoice.setPaymentMethod(paymentMethod);
        
        // Set associations
        invoice.setVehicle(vehicle);
        invoice.setClient(client);
        invoice.setSalesRep(salesRep);
        
        // Calculate financials
        BigDecimal subtotal = sellingPrice;
        BigDecimal itbisAmount = subtotal.multiply(ITBIS_RATE).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal total = subtotal.add(itbisAmount);
        
        invoice.setSubtotal(subtotal);
        invoice.setItbisRate(ITBIS_RATE);
        invoice.setItbisAmount(itbisAmount);
        invoice.setTotal(total);
        invoice.setItbisStatus("GRAVADO"); // Taxable
        
        // Create invoice item
        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setItemType("VEHICLE");
        item.setDescription("Vehículo: " + vehicle.getMake() + " " + vehicle.getModel() + 
                           " (" + vehicle.getVin() + ")");
        item.setQuantity(1);
        item.setUnitPrice(sellingPrice);
        item.setSubtotal(subtotal);
        item.setItbisRate(ITBIS_RATE);
        item.setItbisAmount(itbisAmount);
        item.setTotal(total);
        item.setItbisStatus("GRAVADO");
        
        invoice.getItems().add(item);
        
        // Save invoice
        return invoiceRepository.save(invoice);
    }
    
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }
    
    public Invoice getInvoiceByEnNcf(String enNcf) {
        return invoiceRepository.findByEnNcf(enNcf)
            .orElseThrow(() -> new RuntimeException("Invoice not found with NCF: " + enNcf));
    }
    
    public Page<Invoice> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }
    
    public Page<Invoice> searchInvoices(String search, Pageable pageable) {
        return invoiceRepository.searchInvoices(search, pageable);
    }
    
    @Transactional
    public Invoice markAsPaid(Long id) {
        Invoice invoice = getInvoiceById(id);
        
        if ("CANCELLED".equals(invoice.getStatus())) {
            throw new RuntimeException("Cannot mark cancelled invoice as paid");
        }
        
        invoice.setStatus("PAID");
        return invoiceRepository.save(invoice);
    }
    
    @Transactional
    public Invoice cancelInvoice(Long id, String reason, String cancelledBy) {
        Invoice invoice = getInvoiceById(id);
        
        if ("PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("Cannot cancel paid invoice. Issue a credit note instead.");
        }
        
        invoice.setStatus("CANCELLED");
        invoice.setCancellationReason(reason);
        invoice.setCancelledAt(LocalDateTime.now());
        invoice.setCancelledBy(cancelledBy);
        
        return invoiceRepository.save(invoice);
    }
    
    public Map<String, Object> getInvoiceStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get monthly stats
        List<Object[]> monthlyStats = invoiceRepository.getMonthlyInvoiceStats();
        if (!monthlyStats.isEmpty()) {
            Object[] statsData = monthlyStats.get(0);
            stats.put("monthlyCount", statsData[0]);
            stats.put("monthlyRevenue", statsData[1]);
            stats.put("monthlyItbis", statsData[2]);
        }
        
        // Get total revenue
        stats.put("totalRevenue", invoiceRepository.getTotalRevenue());
        stats.put("totalItbisCollected", invoiceRepository.getTotalItbisCollected());
        
        // Count by status
        List<Object[]> statusCounts = invoiceRepository.countByStatus();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] statusCount : statusCounts) {
            statusMap.put((String) statusCount[0], (Long) statusCount[1]);
        }
        stats.put("statusCounts", statusMap);
        
        // Count by NCF type
        List<Object[]> ncfCounts = invoiceRepository.countByNcfType();
        Map<String, Long> ncfMap = new HashMap<>();
        for (Object[] ncfCount : ncfCounts) {
            ncfMap.put((String) ncfCount[0], (Long) ncfCount[1]);
        }
        stats.put("ncfTypeCounts", ncfMap);
        
        // Get sequence information
        stats.put("currentSequenceNumber", sequenceService.getCurrentSequenceNumber());
        stats.put("sequenceValid", sequenceService.validateSequenceRange());
        
        return stats;
    }
    
    public List<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.findByInvoiceDateBetween(startDate, endDate);
    }
}