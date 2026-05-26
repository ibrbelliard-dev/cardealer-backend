package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.*;
import com.cardealer.iotproject.repository.*;
import com.cardealer.iotproject.util.NhtsaApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

@Service
public class NhtsaService {
    
    private static final Logger log = Logger.getLogger(NhtsaService.class.getName());
    
    @Autowired
    private NhtsaApiClient nhtsaApiClient;
    
    @Autowired
    private MakeRepository makeRepository;
    
    @Autowired
    private ModelRepository modelRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private RecallRepository recallRepository;
    
    @Autowired
    private VinDecodeCacheRepository vinDecodeCacheRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Sync all makes from NHTSA
     */
    @Transactional
    public int syncAllMakes() {
        List<Map<String, Object>> makes = nhtsaApiClient.getAllMakes();
        int count = 0;
        
        for (Map<String, Object> makeData : makes) {
            try {
                Integer makeId = (Integer) makeData.get("Make_ID");
                String makeName = (String) makeData.get("Make_Name");
                
                if (!makeRepository.existsById(makeId)) {
                    Make make = new Make();
                    make.setMakeId(makeId);
                    make.setMakeName(makeName);
                    make.setMakeDisplayName(makeName);
                    makeRepository.save(make);
                    count++;
                }
            } catch (Exception e) {
                log.warning("Failed to sync make: " + e.getMessage());
            }
        }
        
        log.info("Synced " + count + " makes from NHTSA");
        return count;
    }
    
    /**
     * Sync models for a specific make
     */
    @Transactional
    public int syncModelsForMake(String makeName) {
        List<Map<String, Object>> models = nhtsaApiClient.getModelsForMake(makeName, null);
        Optional<Make> makeOpt = makeRepository.findByMakeName(makeName);
        
        if (makeOpt.isEmpty()) {
            log.warning("Make not found: " + makeName);
            return 0;
        }
        
        Make make = makeOpt.get();
        int count = 0;
        
        for (Map<String, Object> modelData : models) {
            try {
                Integer modelId = (Integer) modelData.get("Model_ID");
                String modelName = (String) modelData.get("Model_Name");
                String vehicleType = (String) modelData.get("VehicleType");
                
                if (!modelRepository.existsById(modelId)) {
                    Model model = new Model();
                    model.setModelId(modelId);
                    model.setMake(make);
                    model.setModelName(modelName);
                    model.setVehicleType(vehicleType);
                    modelRepository.save(model);
                    count++;
                }
            } catch (Exception e) {
                log.warning("Failed to sync model: " + e.getMessage());
            }
        }
        
        log.info("Synced " + count + " models for make " + makeName);
        return count;
    }
    
    /**
     * Decode VIN number with truncation
     */
    @Cacheable(value = "vinDecode", key = "#vin")
    @Transactional
    public Map<String, Object> decodeVin(String vin, Integer modelYear) {
        if (vin == null || vin.trim().isEmpty()) {
            log.warning("Empty VIN provided");
            return new HashMap<>();
        }
        
        // Check cache first
        Optional<VinDecodeCache> cached = vinDecodeCacheRepository.findById(vin.toUpperCase());
        if (cached.isPresent()) {
            String decodedJson = cached.get().getDecodedData();
            try {
                Map<String, Object> decodedMap = objectMapper.readValue(decodedJson, new TypeReference<Map<String, Object>>() {});
                log.info("Retrieved VIN " + vin + " from cache");
                return decodedMap;
            } catch (Exception e) {
                log.warning("Failed to parse cached VIN data: " + e.getMessage());
            }
        }
        
        // Call NHTSA API
        Map<String, Object> decodedData = nhtsaApiClient.decodeVin(vin, modelYear);
        
        // Validate response
        if (decodedData == null || decodedData.isEmpty()) {
            log.warning("Empty response from NHTSA for VIN: " + vin);
            return new HashMap<>();
        }
        
        // TRUNCATE THE DATA BEFORE CACHING AND SAVING
        truncateDecodedData(decodedData);
        
        // Cache the result
        try {
            VinDecodeCache cache = new VinDecodeCache();
            cache.setVin(vin.toUpperCase());
            cache.setDecodedData(objectMapper.writeValueAsString(decodedData));
            vinDecodeCacheRepository.save(cache);
        } catch (Exception e) {
            log.warning("Failed to cache VIN data: " + e.getMessage());
        }
        
        // Create or update vehicle only if we have valid data
        String make = getStringValue(decodedData.get("Make"));
        String model = getStringValue(decodedData.get("Model"));
        
        if (make != null && !make.isEmpty() && model != null && !model.isEmpty()) {
            createOrUpdateVehicleFromDecode(decodedData);
        } else {
            log.warning("Incomplete VIN decode data for VIN: " + vin);
        }
        
        return decodedData;
    }
    
    /**
     * Truncate string fields in the decoded data to match database column limits
     */
    private void truncateDecodedData(Map<String, Object> decodedData) {
        if (decodedData == null) return;
        
        // Database column limits
        truncateField(decodedData, "DriveType", 20);
        truncateField(decodedData, "BodyClass", 100);
        truncateField(decodedData, "FuelTypePrimary", 50);
        truncateField(decodedData, "TransmissionStyle", 100);
        truncateField(decodedData, "PlantCity", 100);
        truncateField(decodedData, "PlantCountry", 100);
        truncateField(decodedData, "PlantState", 50);
        truncateField(decodedData, "Make", 100);
        truncateField(decodedData, "Model", 100);
        truncateField(decodedData, "Manufacturer", 100);
        truncateField(decodedData, "Series", 100);
        truncateField(decodedData, "Trim", 100);
        truncateField(decodedData, "VehicleType", 50);
    }
    
    /**
     * Helper method to truncate a specific field in the map
     */
    private void truncateField(Map<String, Object> data, String fieldName, int maxLength) {
        if (data.containsKey(fieldName)) {
            Object value = data.get(fieldName);
            if (value instanceof String) {
                String stringValue = (String) value;
                if (stringValue != null && !stringValue.isEmpty() && stringValue.length() > maxLength) {
                    data.put(fieldName, stringValue.substring(0, maxLength));
                    log.fine("Truncated field '" + fieldName + "' to " + maxLength + " characters");
                }
            }
        }
    }
    
    /**
     * Create or update vehicle from decoded VIN data (with truncation)
     */
    @Transactional
    public void createOrUpdateVehicleFromDecode(Map<String, Object> decodedData) {
        String vin = getStringValue(decodedData.get("VIN"));
        if (vin == null || vin.isEmpty()) {
            log.warning("No VIN in decoded data");
            return;
        }
        
        String makeName = getStringValue(decodedData.get("Make"));
        String modelName = getStringValue(decodedData.get("Model"));
        
        if (makeName == null || modelName == null) {
            log.warning("Missing make or model for VIN: " + vin);
            return;
        }
        
        Integer modelYear = safeParseInt(decodedData.get("ModelYear"));
        
        // Get or create make
        Make make = makeRepository.findByMakeName(makeName)
            .orElseGet(() -> {
                Make newMake = new Make();
                newMake.setMakeName(makeName);
                newMake.setMakeDisplayName(makeName);
                return makeRepository.save(newMake);
            });
        
        // Get or create model
        Model model = modelRepository.findByMakeAndModelName(make, modelName)
            .orElseGet(() -> {
                Model newModel = new Model();
                newModel.setMake(make);
                newModel.setModelName(modelName);
                return modelRepository.save(newModel);
            });
        
        // Create or update vehicle
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElse(new Vehicle());
        
        vehicle.setVin(vin);
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setModelYear(modelYear);
        
        // Set decoded JSON
        try {
            vehicle.setVinDecodedJson(objectMapper.writeValueAsString(decodedData));
        } catch (Exception e) {
            vehicle.setVinDecodedJson(decodedData.toString());
        }
        
        // Set technical specifications with truncation
        vehicle.setBodyClass(truncateString(getStringValue(decodedData.get("BodyClass")), 100));
        vehicle.setDriveType(truncateString(getStringValue(decodedData.get("DriveType")), 20));
        vehicle.setEngineCylinders(safeParseInt(decodedData.get("EngineCylinders")));
        vehicle.setEngineDisplacementL(safeParseBigDecimal(decodedData.get("DisplacementL")));
        vehicle.setFuelTypePrimary(truncateString(getStringValue(decodedData.get("FuelTypePrimary")), 50));
        vehicle.setTransmissionStyle(truncateString(getStringValue(decodedData.get("TransmissionStyle")), 100));
        vehicle.setDoors(safeParseInt(decodedData.get("Doors")));
        vehicle.setPlantCity(truncateString(getStringValue(decodedData.get("PlantCity")), 100));
        vehicle.setPlantState(truncateString(getStringValue(decodedData.get("PlantState")), 50));
        vehicle.setPlantCountry(truncateString(getStringValue(decodedData.get("PlantCountry")), 100));
        vehicle.setSeries(truncateString(getStringValue(decodedData.get("Series")), 100));
        vehicle.setTrim(truncateString(getStringValue(decodedData.get("Trim")), 100));
        vehicle.setVehicleCurbWeightLbs(safeParseInt(decodedData.get("CurbWeightLB")));
        
        // Set default values for required fields
        if (vehicle.getDateAdded() == null) {
            vehicle.setDateAdded(java.time.LocalDateTime.now());
        }
        if (vehicle.getIsActive() == null) {
            vehicle.setIsActive(true);
        }
        if (vehicle.getStatus() == null) {
            vehicle.setStatus(com.cardealer.iotproject.model.enums.VehicleStatus.AVAILABLE);
        }
        if (vehicle.getLastModified() == null) {
            vehicle.setLastModified(java.time.LocalDateTime.now());
        }
        
        vehicleRepository.save(vehicle);
        log.info("Created/updated vehicle with VIN: " + vin);
    }
    
    /**
     * Get recalls for a specific vehicle
     */
    @Transactional(readOnly = true)
    public List<Recall> getRecallsForVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        
        // Check database first
        List<Recall> existingRecalls = recallRepository.findRecallsForVehicle(
            vehicle.getMake() != null ? vehicle.getMake().getMakeId() : null,
            vehicle.getModel() != null ? vehicle.getModel().getModelId() : null,
            vehicle.getModelYear()
        );
        
        // If no recalls found, fetch from NHTSA API
        if (existingRecalls.isEmpty()) {
            String makeName = vehicle.getMake() != null ? vehicle.getMake().getMakeName() : null;
            String modelName = vehicle.getModel() != null ? vehicle.getModel().getModelName() : null;
            
            if (makeName != null && modelName != null && vehicle.getModelYear() != null) {
                List<Map<String, Object>> recallData = nhtsaApiClient.getRecallsByVehicle(
                    makeName, modelName, vehicle.getModelYear()
                );
                
                for (Map<String, Object> data : recallData) {
                    try {
                        Recall recall = new Recall();
                        recall.setNhtsaCampaignNumber(getStringValue(data.get("NHTSACampaignNumber")));
                        recall.setMake(vehicle.getMake());
                        recall.setModel(vehicle.getModel());
                        recall.setComponent(getStringValue(data.get("Component")));
                        recall.setSummary(getStringValue(data.get("Summary")));
                        recall.setConsequence(getStringValue(data.get("Consequence")));
                        recall.setRemedy(getStringValue(data.get("Remedy")));
                        
                        String reportDate = getStringValue(data.get("ReportReceivedDate"));
                        if (reportDate != null && !reportDate.isEmpty()) {
                            try {
                                recall.setReportReceivedDate(LocalDate.parse(reportDate));
                            } catch (Exception e) {
                                log.warning("Failed to parse report date: " + reportDate);
                            }
                        }
                        
                        recallRepository.save(recall);
                        existingRecalls.add(recall);
                    } catch (Exception e) {
                        log.warning("Failed to save recall: " + e.getMessage());
                    }
                }
            }
        }
        
        return existingRecalls;
    }
    
    /**
     * Helper method to truncate a string to max length
     */
    private String truncateString(String value, int maxLength) {
        if (value == null) return null;
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength);
    }
    
    /**
     * Helper method to safely get string values
     */
    private String getStringValue(Object value) {
        if (value == null) return null;
        String str = value.toString();
        if (str.trim().isEmpty()) return null;
        return str;
    }
    
    /**
     * Helper method to safely parse integers
     */
    private Integer safeParseInt(Object value) {
        if (value == null) return null;
        String str = value.toString().trim();
        if (str.isEmpty()) return null;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            log.warning("Failed to parse integer: " + str);
            return null;
        }
    }
    
    /**
     * Helper method to safely parse BigDecimal
     */
    private BigDecimal safeParseBigDecimal(Object value) {
        if (value == null) return null;
        String str = value.toString().trim();
        if (str.isEmpty()) return null;
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            log.warning("Failed to parse decimal: " + str);
            return null;
        }
    }
}