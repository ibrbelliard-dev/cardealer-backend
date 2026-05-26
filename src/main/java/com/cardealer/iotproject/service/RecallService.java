package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.Recall;
import com.cardealer.iotproject.model.entity.Vehicle;
import com.cardealer.iotproject.repository.RecallRepository;
import com.cardealer.iotproject.repository.VehicleRecallLinkRepository;
import com.cardealer.iotproject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RecallService {
    
    private static final Logger log = Logger.getLogger(RecallService.class.getName());
    
    @Autowired
    private RecallRepository recallRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private VehicleRecallLinkRepository vehicleRecallLinkRepository;
    
    @Autowired
    private NhtsaService nhtsaService;
    
    @Transactional
    public Recall saveRecall(Recall recall) {
        if (recall.getNhtsaCampaignNumber() != null) {
            recallRepository.findByNhtsaCampaignNumber(recall.getNhtsaCampaignNumber())
                .ifPresent(existing -> {
                    throw new RuntimeException("Recall with campaign number " + 
                        recall.getNhtsaCampaignNumber() + " already exists");
                });
        }
        return recallRepository.save(recall);
    }
    
    @Transactional(readOnly = true)
    public Recall getRecallById(Long id) {
        return recallRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recall not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public Recall getRecallByCampaignNumber(String campaignNumber) {
        return recallRepository.findByNhtsaCampaignNumber(campaignNumber)
            .orElseThrow(() -> new RuntimeException("Recall not found with campaign number: " + campaignNumber));
    }
    
    @Transactional(readOnly = true)
    public List<Recall> getRecallsForVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        
        return recallRepository.findRecallsForVehicle(
            vehicle.getMake() != null ? vehicle.getMake().getMakeId() : null,
            vehicle.getModel() != null ? vehicle.getModel().getModelId() : null,
            vehicle.getModelYear()
        );
    }
    
    @Transactional(readOnly = true)
    public Page<Recall> searchRecalls(String makeName, String modelName, Integer year, Pageable pageable) {
        return recallRepository.searchRecalls(makeName, modelName, year, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Recall> getRecentRecalls(int limit) {
        return recallRepository.findByReportReceivedDateAfter(LocalDate.now().minusMonths(6))
            .stream()
            .limit(limit)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public List<Recall> getRecallsByComponent(String component) {
        return recallRepository.findByComponentContainingIgnoreCase(component);
    }
    
    @Transactional(readOnly = true)
    public List<Recall> getRecallsByMake(Integer makeId) {
        return recallRepository.findByMake_MakeId(makeId);
    }
    
    @Transactional(readOnly = true)
    public List<Recall> getRecallsByMakeAndModel(Integer makeId, Integer modelId) {
        return recallRepository.findByMake_MakeIdAndModel_ModelId(makeId, modelId);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getRecallStatsByComponent() {
        return recallRepository.getRecallCountByComponent();
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getRecallStatsByYear() {
        return recallRepository.getRecallCountByYear();
    }
    
    @Transactional
    public Recall updateRecall(Long id, Recall recallDetails) {
        Recall existing = getRecallById(id);
        
        existing.setComponent(recallDetails.getComponent());
        existing.setSummary(recallDetails.getSummary());
        existing.setConsequence(recallDetails.getConsequence());
        existing.setRemedy(recallDetails.getRemedy());
        existing.setRecallStatus(recallDetails.getRecallStatus());
        existing.setManufacturerNotes(recallDetails.getManufacturerNotes());
        
        return recallRepository.save(existing);
    }
    
    @Transactional
    public void deleteRecall(Long id) {
        Recall recall = getRecallById(id);
        recallRepository.delete(recall);
        log.info("Deleted recall with id: " + id);
    }
}