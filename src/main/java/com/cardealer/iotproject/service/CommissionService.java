package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.dto.*;
import com.cardealer.iotproject.model.entity.Commission;
import com.cardealer.iotproject.repository.CommissionRepository;
import com.cardealer.iotproject.repository.SalesRepRepository;
import com.cardealer.iotproject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class CommissionService {
    
    @Autowired
    private CommissionRepository commissionRepository;
    
    @Autowired
    private SalesRepRepository salesRepRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Transactional
    public Commission createCommission(Long salesRepId, Long vehicleId, Long invoiceId, BigDecimal salePrice) {
        // Verificar si ya existe comisión
        if (commissionRepository.existsByVehicleId(vehicleId)) {
            throw new RuntimeException("Ya existe una comisión para este vehículo");
        }
        
        // Obtener porcentaje de comisión del vendedor
        BigDecimal commissionPercentage = BigDecimal.ZERO;
        var salesRepOpt = salesRepRepository.findById(salesRepId);
        if (salesRepOpt.isPresent()) {
            commissionPercentage = salesRepOpt.get().getCommissionPercentage();
        }
        
        // Calcular monto de comisión
        BigDecimal commissionAmount = salePrice
            .multiply(commissionPercentage)
            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        
        // Crear comisión
        Commission commission = new Commission();
        commission.setSalesRepId(salesRepId);
        commission.setVehicleId(vehicleId);
        commission.setInvoiceId(invoiceId);
        commission.setSalePrice(salePrice);
        commission.setCommissionPercentage(commissionPercentage);
        commission.setCommissionAmount(commissionAmount);
        commission.setStatus("PENDING");
        
        return commissionRepository.save(commission);
    }
    
    @Transactional
    public Commission payCommission(CommissionPaymentRequest request) {
        Commission commission = commissionRepository.findById(request.getCommissionId())
            .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));
        
        if (!"PENDING".equals(commission.getStatus())) {
            throw new RuntimeException("La comisión ya fue procesada");
        }
        
        commission.setStatus("PAID");
        commission.setPaymentDate(LocalDateTime.now());
        commission.setPaymentReference(request.getPaymentReference());
        commission.setPaidBy(request.getPaidBy());
        if (request.getNotes() != null) {
            commission.setNotes(request.getNotes());
        }
        
        return commissionRepository.save(commission);
    }
    
    @Transactional
    public Commission cancelCommission(Long commissionId, String reason) {
        Commission commission = commissionRepository.findById(commissionId)
            .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));
        
        if ("PAID".equals(commission.getStatus())) {
            throw new RuntimeException("No se puede cancelar una comisión ya pagada");
        }
        
        commission.setStatus("CANCELLED");
        String cancelNote = "Cancelada: " + reason;
        String currentNotes = commission.getNotes();
        commission.setNotes(currentNotes != null ? currentNotes + " | " + cancelNote : cancelNote);
        
        return commissionRepository.save(commission);
    }
    
    public CommissionDTO getCommissionById(Long id) {
        Commission commission = commissionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));
        return convertToDTO(commission);
    }
    
    public Page<CommissionDTO> listCommissions(CommissionFilterRequest filter) {
        Pageable pageable = PageRequest.of(
            filter.getPage(),
            filter.getSize(),
            Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy())
        );
        
        Page<Commission> commissionsPage;
        
        if (filter.getSalesRepId() != null && filter.getStatus() != null) {
            commissionsPage = commissionRepository.findBySalesRepIdAndStatus(
                filter.getSalesRepId(), filter.getStatus(), pageable);
        } else if (filter.getSalesRepId() != null) {
            commissionsPage = commissionRepository.findBySalesRepId(filter.getSalesRepId(), pageable);
        } else if (filter.getStatus() != null) {
            commissionsPage = commissionRepository.findByStatus(filter.getStatus(), pageable);
        } else if (filter.getStartDate() != null && filter.getEndDate() != null) {
            LocalDateTime start = filter.getStartDate().atStartOfDay();
            LocalDateTime end = filter.getEndDate().atTime(LocalTime.MAX);
            commissionsPage = commissionRepository.findByCreatedAtBetween(start, end, pageable);
        } else {
            commissionsPage = commissionRepository.findAll(pageable);
        }
        
        return commissionsPage.map(this::convertToDTO);
    }
    
    public CommissionDashboardDTO getDashboardStats() {
        CommissionDashboardDTO stats = new CommissionDashboardDTO();
        
        stats.setTotalPending(commissionRepository.getTotalPendingAll());
        stats.setTotalPaid(commissionRepository.getTotalPaidAll());
        stats.setTotalPendingCount(commissionRepository.countPendingAll());
        stats.setTotalPaidCount(commissionRepository.countPaidAll());
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();
        stats.setMonthCommissions(commissionRepository.getTotalBetweenDates(startOfMonth, now));
        
        return stats;
    }
    
    private CommissionDTO convertToDTO(Commission commission) {
        CommissionDTO dto = new CommissionDTO();
        dto.setId(commission.getId());
        dto.setSalesRepId(commission.getSalesRepId());
        dto.setVehicleId(commission.getVehicleId());
        dto.setInvoiceId(commission.getInvoiceId());
        dto.setSalePrice(commission.getSalePrice());
        dto.setCommissionPercentage(commission.getCommissionPercentage());
        dto.setCommissionAmount(commission.getCommissionAmount());
        dto.setStatus(commission.getStatus());
        dto.setPaymentDate(commission.getPaymentDate());
        dto.setPaymentReference(commission.getPaymentReference());
        dto.setNotes(commission.getNotes());
        dto.setCreatedAt(commission.getCreatedAt());
        dto.setPaidBy(commission.getPaidBy());
        
        // Obtener nombre del vendedor
        salesRepRepository.findById(commission.getSalesRepId()).ifPresent(rep -> {
            dto.setSalesRepName(rep.getFirstName() + " " + rep.getLastName());
            dto.setSalesRepCedula(rep.getCedula());
        });
        
        // Obtener descripción del vehículo
        vehicleRepository.findById(commission.getVehicleId()).ifPresent(vehicle -> {
            String desc = "";
            if (vehicle.getMake() != null) desc += vehicle.getMake().getMakeName() + " ";
            if (vehicle.getModel() != null) desc += vehicle.getModel().getModelName();
            desc += " (" + vehicle.getModelYear() + ")";
            dto.setVehicleDescription(desc);
            dto.setVin(vehicle.getVin());
        });
        
        return dto;
    }
}