package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.SalesRep;
import com.cardealer.iotproject.repository.SalesRepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Service
public class SalesRepService {
    
    private static final Logger log = Logger.getLogger(SalesRepService.class.getName());
    
    @Autowired
    private SalesRepRepository salesRepRepository;
    
    @Transactional(readOnly = true)
    public List<SalesRep> getAllSalesReps() {
        return salesRepRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<SalesRep> getAllSalesReps(Pageable pageable) {
        return salesRepRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public SalesRep getSalesRepById(Long id) {
        return salesRepRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Vendedor no encontrado con ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<SalesRep> getActiveSalesReps() {
        return salesRepRepository.findByStatus(1);
    }
    
    @Transactional
    public SalesRep createSalesRep(SalesRep salesRep) {
        // Validate unique cedula
        if (salesRep.getCedula() != null && !salesRep.getCedula().isEmpty()) {
            if (salesRepRepository.existsByCedula(salesRep.getCedula())) {
                throw new RuntimeException("Ya existe un vendedor con la cédula: " + salesRep.getCedula());
            }
        }
        
        // Validate unique email
        if (salesRep.getEmail() != null && !salesRep.getEmail().isEmpty()) {
            if (salesRepRepository.existsByEmail(salesRep.getEmail())) {
                throw new RuntimeException("Ya existe un vendedor con el email: " + salesRep.getEmail());
            }
        }
        
        if (salesRep.getHireDate() == null) {
            salesRep.setHireDate(LocalDate.now());
        }
        
        salesRep.setStatus(1);
        salesRep.setTotalSales(BigDecimal.ZERO);
        
        SalesRep saved = salesRepRepository.save(salesRep);
        log.info("Vendedor creado: " + saved.getFullName());
        return saved;
    }
    
    @Transactional
    public SalesRep updateSalesRep(Long id, SalesRep salesRepDetails) {
        SalesRep salesRep = getSalesRepById(id);
        
        if (salesRepDetails.getFirstName() != null) {
            salesRep.setFirstName(salesRepDetails.getFirstName());
        }
        if (salesRepDetails.getLastName() != null) {
            salesRep.setLastName(salesRepDetails.getLastName());
        }
        if (salesRepDetails.getCedula() != null) {
            if (!salesRepDetails.getCedula().equals(salesRep.getCedula()) && 
                salesRepRepository.existsByCedula(salesRepDetails.getCedula())) {
                throw new RuntimeException("Ya existe otro vendedor con la cédula: " + salesRepDetails.getCedula());
            }
            salesRep.setCedula(salesRepDetails.getCedula());
        }
        if (salesRepDetails.getEmail() != null) {
            if (!salesRepDetails.getEmail().equals(salesRep.getEmail()) && 
                salesRepRepository.existsByEmail(salesRepDetails.getEmail())) {
                throw new RuntimeException("Ya existe otro vendedor con el email: " + salesRepDetails.getEmail());
            }
            salesRep.setEmail(salesRepDetails.getEmail());
        }
        if (salesRepDetails.getCellPhone() != null) {
            salesRep.setCellPhone(salesRepDetails.getCellPhone());
        }
        if (salesRepDetails.getBusinessPhone() != null) {
            salesRep.setBusinessPhone(salesRepDetails.getBusinessPhone());
        }
        if (salesRepDetails.getAddress() != null) {
            salesRep.setAddress(salesRepDetails.getAddress());
        }
        if (salesRepDetails.getCommissionPercentage() != null) {
            salesRep.setCommissionPercentage(salesRepDetails.getCommissionPercentage());
        }
        if (salesRepDetails.getSalesQuota() != null) {
            salesRep.setSalesQuota(salesRepDetails.getSalesQuota());
        }
        if (salesRepDetails.getNotes() != null) {
            salesRep.setNotes(salesRepDetails.getNotes());
        }
        
        SalesRep saved = salesRepRepository.save(salesRep);
        log.info("Vendedor actualizado: " + saved.getFullName());
        return saved;
    }
    
    @Transactional
    public SalesRep updateSalesRepStatus(Long id, Integer status) {
        SalesRep salesRep = getSalesRepById(id);
        salesRep.setStatus(status);
        
        if (status == 0) {
            salesRep.setTerminationDate(LocalDate.now());
        } else if (status == 1 && salesRep.getTerminationDate() != null) {
            salesRep.setTerminationDate(null);
        }
        
        return salesRepRepository.save(salesRep);
    }
    
    @Transactional
    public void deleteSalesRep(Long id) {
        SalesRep salesRep = getSalesRepById(id);
        salesRepRepository.delete(salesRep);
        log.info("Vendedor eliminado: " + salesRep.getFullName());
    }
    
    @Transactional
    public void addToTotalSales(Long salesRepId, BigDecimal amount) {
        SalesRep salesRep = getSalesRepById(salesRepId);
        BigDecimal newTotal = salesRep.getTotalSales().add(amount);
        salesRep.setTotalSales(newTotal);
        salesRepRepository.save(salesRep);
    }
    
    @Transactional(readOnly = true)
    public Page<SalesRep> searchSalesReps(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return salesRepRepository.findAll(pageable);
        }
        return salesRepRepository.searchSalesReps(search.trim(), pageable);
    }
    
    @Transactional(readOnly = true)
    public List<SalesRep> getTopPerformers(int limit) {
        return salesRepRepository.findTopPerformingSalesReps(org.springframework.data.domain.PageRequest.of(0, limit));
    }
}