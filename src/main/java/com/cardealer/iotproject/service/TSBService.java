package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.ManufacturerCommunication;
import com.cardealer.iotproject.model.enums.CommType;
import com.cardealer.iotproject.repository.TSBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class TSBService {
    
    private static final Logger log = Logger.getLogger(TSBService.class.getName());
    
    @Autowired
    private TSBRepository tsbRepository;
    
    @Transactional
    public ManufacturerCommunication saveTsb(ManufacturerCommunication tsb) {
        if (tsb.getNhtsaIdNumber() != null) {
            tsbRepository.findByNhtsaIdNumber(tsb.getNhtsaIdNumber())
                .ifPresent(existing -> {
                    throw new RuntimeException("TSB with NHTSA ID " + 
                        tsb.getNhtsaIdNumber() + " already exists");
                });
        }
        return tsbRepository.save(tsb);
    }
    
    @Transactional(readOnly = true)
    public ManufacturerCommunication getTsbById(Long id) {
        return tsbRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("TSB not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public ManufacturerCommunication getTsbByDocumentId(String documentId) {
        return tsbRepository.findByTsbDocumentId(documentId)
            .orElseThrow(() -> new RuntimeException("TSB not found with document ID: " + documentId));
    }
    
    @Transactional(readOnly = true)
    public List<ManufacturerCommunication> getTsbsForVehicle(Integer makeId, Integer modelId, Integer year) {
        return tsbRepository.findTsbsForVehicle(makeId, modelId, year);
    }
    
    @Transactional(readOnly = true)
    public List<ManufacturerCommunication> getTsbsByMake(Integer makeId) {
        return tsbRepository.findByMake_MakeId(makeId);
    }
    
    @Transactional(readOnly = true)
    public List<ManufacturerCommunication> getTsbsByMakeAndModel(Integer makeId, Integer modelId) {
        return tsbRepository.findByMake_MakeIdAndModel_ModelId(makeId, modelId);
    }
    
    @Transactional(readOnly = true)
    public Page<ManufacturerCommunication> searchTsbs(String keyword, Pageable pageable) {
        return tsbRepository.searchTsbs(keyword, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<ManufacturerCommunication> getRecentTsbs(int limit) {
        return tsbRepository.findRecentTsbs(Pageable.ofSize(limit));
    }
    
    @Transactional(readOnly = true)
    public List<ManufacturerCommunication> getTsbsByType(CommType type) {
        return tsbRepository.findByCommunicationType(type);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getTsbCountByComponentSystem() {
        return tsbRepository.getTsbCountByComponentSystem();
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getTsbCountByType() {
        return tsbRepository.getTsbCountByType();
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getTsbCountByMake() {
        return tsbRepository.getTsbCountByMake();
    }
    
    @Transactional(readOnly = true)
    public long getTotalTsbCount() {
        return tsbRepository.count();
    }
    
    @Transactional(readOnly = true)
    public long getTsbCountByMake(Integer makeId) {
        return tsbRepository.countByMake_MakeId(makeId);
    }
    
    @Transactional
    public ManufacturerCommunication updateTsb(Long id, ManufacturerCommunication tsbDetails) {
        ManufacturerCommunication existing = getTsbById(id);
        
        existing.setSummary(tsbDetails.getSummary());
        existing.setNhtsaComponents(tsbDetails.getNhtsaComponents());
        existing.setMfrComponentSystem(tsbDetails.getMfrComponentSystem());
        existing.setMfrComponentSubsystem(tsbDetails.getMfrComponentSubsystem());
        existing.setLaborHours(tsbDetails.getLaborHours());
        existing.setPartsRequired(tsbDetails.getPartsRequired());
        existing.setRepairProcedure(tsbDetails.getRepairProcedure());
        existing.setDocumentUrl(tsbDetails.getDocumentUrl());
        
        return tsbRepository.save(existing);
    }
    
    @Transactional
    public void deleteTsb(Long id) {
        ManufacturerCommunication tsb = getTsbById(id);
        tsbRepository.delete(tsb);
        log.info("Deleted TSB with id: " + id);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getTsbStatistics() {
        List<Object[]> summary = tsbRepository.getTsbSummaryStatistics();
        Map<String, Object> stats = new java.util.HashMap<>();
        
        if (!summary.isEmpty()) {
            Object[] data = summary.get(0);
            stats.put("totalTsbs", data[0]);
            stats.put("affectedMakes", data[1]);
            stats.put("affectedModels", data[2]);
            stats.put("latestTsbDate", data[3]);
            stats.put("earliestTsbDate", data[4]);
        }
        
        stats.put("tsbsByType", getTsbCountByType());
        stats.put("tsbsByComponent", getTsbCountByComponentSystem().stream().limit(10).toList());
        
        return stats;
    }
    
    @Transactional(readOnly = true)
    public List<ManufacturerCommunication> getTsbsByDateRange(LocalDate startDate, LocalDate endDate) {
        return tsbRepository.findByMfrCommunicationDateBetween(startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public List<ManufacturerCommunication> getLatestTsbs(int limit) {
        return tsbRepository.findLatestTsbs(Pageable.ofSize(limit));
    }
}