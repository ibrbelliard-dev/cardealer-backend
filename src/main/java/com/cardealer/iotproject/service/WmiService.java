package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.WMI;
import com.cardealer.iotproject.repository.WMIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WmiService {
    
    @Autowired
    private WMIRepository wmiRepository;
    
    @Transactional(readOnly = true)
    public List<WMI> getAllWmi() {
        return wmiRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public WMI getWmiByCode(String wmiCode) {
        return wmiRepository.findByWmi(wmiCode)
            .orElseThrow(() -> new RuntimeException("WMI not found: " + wmiCode));
    }
    
    @Transactional(readOnly = true)
    public List<WMI> getWmiByManufacturer(String manufacturerName) {
        return wmiRepository.findByManufacturerNameContainingIgnoreCase(manufacturerName);
    }
    
    @Transactional(readOnly = true)
    public List<WMI> getWmiByCountry(String country) {
        return wmiRepository.findByPlantCountry(country);
    }
    
    @Transactional(readOnly = true)
    public List<WMI> getActiveWmi() {
        return wmiRepository.findByIsActiveTrue();
    }
    
    @Transactional
    public WMI saveWmi(WMI wmi) {
        if (wmi.getWmi() != null && wmiRepository.existsByWmi(wmi.getWmi())) {
            throw new RuntimeException("WMI already exists: " + wmi.getWmi());
        }
        return wmiRepository.save(wmi);
    }
    
    @Transactional
    public WMI updateWmi(Long id, WMI wmiDetails) {
        WMI existing = wmiRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("WMI not found with id: " + id));
        
        existing.setManufacturerName(wmiDetails.getManufacturerName());
        existing.setVehicleType(wmiDetails.getVehicleType());
        existing.setAddress(wmiDetails.getAddress());
        existing.setPlantCity(wmiDetails.getPlantCity());
        existing.setPlantState(wmiDetails.getPlantState());
        existing.setPlantCountry(wmiDetails.getPlantCountry());
        existing.setDateAvailable(wmiDetails.getDateAvailable());
        existing.setDateStopped(wmiDetails.getDateStopped());
        existing.setIsActive(wmiDetails.getIsActive());
        
        return wmiRepository.save(existing);
    }
    
    @Transactional
    public void deactivateWmi(String wmiCode) {
        WMI wmi = getWmiByCode(wmiCode);
        wmi.setIsActive(false);
        wmi.setDateStopped(java.time.LocalDate.now());
        wmiRepository.save(wmi);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Long> getWmiCountByCountry() {
        return wmiRepository.countWmiByCountry().stream()
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> (Long) arr[1]
            ));
    }
    
    @Transactional(readOnly = true)
    public Map<String, Long> getWmiCountByManufacturer() {
        return wmiRepository.countWmiByManufacturer().stream()
            .limit(10)
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> (Long) arr[1]
            ));
    }
}