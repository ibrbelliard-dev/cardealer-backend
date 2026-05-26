package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.Complaint;
import com.cardealer.iotproject.repository.ComplaintRepository;
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
public class ComplaintService {
    
    private static final Logger log = Logger.getLogger(ComplaintService.class.getName());
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Transactional
    public Complaint saveComplaint(Complaint complaint) {
        if (complaint.getOdiNumber() != null) {
            complaintRepository.findByOdiNumber(complaint.getOdiNumber())
                .ifPresent(existing -> {
                    throw new RuntimeException("Complaint with ODI number " + 
                        complaint.getOdiNumber() + " already exists");
                });
        }
        return complaintRepository.save(complaint);
    }
    
    @Transactional(readOnly = true)
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public Complaint getComplaintByOdiNumber(String odiNumber) {
        return complaintRepository.findByOdiNumber(odiNumber)
            .orElseThrow(() -> new RuntimeException("Complaint not found with ODI: " + odiNumber));
    }
    
    @Transactional(readOnly = true)
    public List<Complaint> getComplaintsForVehicle(Long vehicleId) {
        return complaintRepository.findByVehicle_VehicleId(vehicleId);
    }
    
    @Transactional(readOnly = true)
    public List<Complaint> getComplaintsForMakeModelYear(Integer makeId, Integer modelId, Integer year) {
        return complaintRepository.findByMake_MakeIdAndModel_ModelIdAndModelYear(makeId, modelId, year);
    }
    
    @Transactional(readOnly = true)
    public Page<Complaint> searchComplaints(String searchTerm, Pageable pageable) {
        return complaintRepository.searchComplaints(searchTerm, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getTopComplaintComponents() {
        return complaintRepository.getTopComplaintComponents();
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getComplaintCountByMake() {
        return complaintRepository.getComplaintCountByMake();
    }
    
    @Transactional(readOnly = true)
    public long getTotalInjuries() {
        return complaintRepository.getTotalInjuries();
    }
    
    @Transactional(readOnly = true)
    public long getTotalFatalities() {
        return complaintRepository.getTotalFatalities();
    }
    
    @Transactional(readOnly = true)
    public Page<Complaint> getRecentComplaints(int limit) {
        return complaintRepository.findRecentComplaints(Pageable.ofSize(limit));
    }
    
    @Transactional(readOnly = true)
    public Page<Complaint> getSevereComplaints(int limit) {
        return (Page<Complaint>) complaintRepository.findSevereComplaints(Pageable.ofSize(limit));
    }
    
    @Transactional
    public Complaint updateComplaint(Long id, Complaint complaintDetails) {
        Complaint existing = getComplaintById(id);
        
        existing.setComponent(complaintDetails.getComponent());
        existing.setComplaintText(complaintDetails.getComplaintText());
        existing.setManufacturerResponse(complaintDetails.getManufacturerResponse());
        existing.setIsClosed(complaintDetails.getIsClosed());
        existing.setInvestigationStatus(complaintDetails.getInvestigationStatus());
        existing.setCrashFlag(complaintDetails.getCrashFlag());
        existing.setFireFlag(complaintDetails.getFireFlag());
        existing.setInjuriesCount(complaintDetails.getInjuriesCount());
        existing.setDeathsCount(complaintDetails.getDeathsCount());
        
        return complaintRepository.save(existing);
    }
    
    @Transactional
    public void deleteComplaint(Long id) {
        Complaint complaint = getComplaintById(id);
        complaintRepository.delete(complaint);
        log.info("Deleted complaint with id: " + id);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getComplaintStatistics() {
        return Map.of(
            "totalComplaints", complaintRepository.count(),
            "totalInjuries", complaintRepository.getTotalInjuries(),
            "totalFatalities", complaintRepository.getTotalFatalities(),
            "topComponents", complaintRepository.getTopComplaintComponents().stream().limit(5).toList(),
            "complaintsByMake", complaintRepository.getComplaintCountByMake()
        );
    }
}