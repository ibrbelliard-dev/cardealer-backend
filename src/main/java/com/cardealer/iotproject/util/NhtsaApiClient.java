package com.cardealer.iotproject.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.logging.Logger;

@Component
public class NhtsaApiClient {
    
    private static final Logger log = Logger.getLogger(NhtsaApiClient.class.getName());
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${nhtsa.api.base-url:https://vpic.nhtsa.dot.gov/api}")
    private String vpicBaseUrl;
    
    @Value("${nhtsa.recalls.base-url:https://api.nhtsa.gov/recalls}")
    private String recallsBaseUrl;
    
    @Value("${nhtsa.complaints.base-url:https://api.nhtsa.gov/complaints}")
    private String complaintsBaseUrl;
    
    @Value("${nhtsa.timeout:30000}")
    private int timeout;
    
    public NhtsaApiClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get all vehicle makes from NHTSA
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllMakes() {
        String url = vpicBaseUrl + "/vehicles/GetAllMakes?format=json";
        return fetchResults(url);
    }
    
    /**
     * Get models for a specific make
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getModelsForMake(String makeName, Integer year) {
        String url;
        if (year != null) {
            url = String.format("%s/vehicles/GetModelsForMakeYear/make/%s/modelyear/%d?format=json",
                vpicBaseUrl, makeName, year);
        } else {
            url = String.format("%s/vehicles/GetModelsForMake/%s?format=json", vpicBaseUrl, makeName);
        }
        return fetchResults(url);
    }
    
    /**
     * Get models for a specific make and year
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getModelsForMakeAndYear(String makeName, int year) {
        String url = String.format("%s/vehicles/GetModelsForMakeYear/make/%s/modelyear/%d?format=json",
            vpicBaseUrl, makeName, year);
        return fetchResults(url);
    }
    
    /**
     * Decode a VIN number (complete extended decode)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> decodeVin(String vin, Integer modelYear) {
        String url = String.format("%s/vehicles/DecodeVinValuesExtended/%s?format=json", vpicBaseUrl, vin);
        if (modelYear != null) {
            url += "&modelyear=" + modelYear;
        }
        List<Map<String, Object>> results = fetchResults(url);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
    
    /**
     * Decode VIN basic (without extended values)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> decodeVinBasic(String vin) {
        String url = String.format("%s/vehicles/DecodeVinValues/%s?format=json", vpicBaseUrl, vin);
        List<Map<String, Object>> results = fetchResults(url);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
    
    /**
     * Get all manufacturers
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllManufacturers() {
        String url = vpicBaseUrl + "/vehicles/GetAllManufacturers?format=json";
        return fetchResults(url);
    }
    
    /**
     * Get manufacturer by ID
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getManufacturerById(int manufacturerId) {
        String url = String.format("%s/vehicles/GetManufacturerById/%d?format=json", vpicBaseUrl, manufacturerId);
        List<Map<String, Object>> results = fetchResults(url);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
    
    /**
     * Get manufacturer by name
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getManufacturerByName(String manufacturerName) {
        String url = String.format("%s/vehicles/GetManufacturerDetails/%s?format=json", vpicBaseUrl, manufacturerName);
        return fetchResults(url);
    }
    
    /**
     * Get vehicle types for a make
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getVehicleTypesForMake(String makeName) {
        String url = String.format("%s/vehicles/GetVehicleTypesForMake/%s?format=json", vpicBaseUrl, makeName);
        return fetchResults(url);
    }
    
    /**
     * Get vehicle types for a make ID
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getVehicleTypesForMakeId(int makeId) {
        String url = String.format("%s/vehicles/GetVehicleTypesForMakeId/%d?format=json", vpicBaseUrl, makeId);
        return fetchResults(url);
    }
    
    /**
     * Get all vehicle variable list
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getVehicleVariableList() {
        String url = vpicBaseUrl + "/vehicles/GetVehicleVariableList?format=json";
        return fetchResults(url);
    }
    
    /**
     * Decode WMI (World Manufacturer Identifier)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> decodeWmi(String wmi) {
        String url = String.format("%s/vehicles/DecodeWMI/%s?format=json", vpicBaseUrl, wmi);
        return fetchResults(url);
    }
    
    /**
     * Get all WMIs (World Manufacturer Identifiers)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllWmis() {
        String url = vpicBaseUrl + "/vehicles/GetAllWMIs?format=json";
        return fetchResults(url);
    }
    
    /**
     * Get recalls by vehicle (make, model, year)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getRecallsByVehicle(String make, String model, Integer year) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(recallsBaseUrl + "/recalls/recallsbyvehicle")
                .queryParam("make", make);
            
            if (model != null && !model.isEmpty()) {
                builder.queryParam("model", model);
            }
            if (year != null) {
                builder.queryParam("modelYear", year);
            }
            
            String url = builder.toUriString();
            return fetchResults(url);
        } catch (Exception e) {
            log.severe("Error fetching recalls for vehicle: " + make + " " + model + " " + year + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get recall by campaign number
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRecallByCampaign(String campaignNumber) {
        try {
            String url = recallsBaseUrl + "/recalls/campaign?campaignNumber=" + campaignNumber;
            List<Map<String, Object>> results = fetchResults(url);
            return results.isEmpty() ? new HashMap<>() : results.get(0);
        } catch (Exception e) {
            log.severe("Error fetching recall by campaign: " + campaignNumber + " - " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Get complaints for a vehicle
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getComplaintsByVehicle(String make, String model, Integer year) {
        try {
            String url = String.format("%s/complaints?make=%s&model=%s&year=%d", 
                complaintsBaseUrl, make, model, year);
            return fetchResults(url);
        } catch (Exception e) {
            log.severe("Error fetching complaints for vehicle: " + make + " " + model + " " + year + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get equipment plant codes for a specific year
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEquipmentPlantCodes(int year) {
        String url = String.format("%s/vehicles/GetEquipmentPlantCodes/%d?format=json", vpicBaseUrl, year);
        return fetchResults(url);
    }
    
    /**
     * Get Canadian vehicle specifications
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCanadianVehicleSpecs(String vin) {
        String url = String.format("%s/vehicles/DecodeVinValues/%s?format=json&modelYear=&isCanada=true", 
            vpicBaseUrl, vin);
        return fetchResults(url);
    }
    
    /**
     * Batch decode multiple VINs (up to 50)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> batchDecodeVins(List<String> vins) {
        if (vins == null || vins.isEmpty() || vins.size() > 50) {
            log.warning("Batch decode requires 1-50 VINs. Received: " + (vins == null ? 0 : vins.size()));
            return new ArrayList<>();
        }
        
        try {
            String vinString = String.join(";", vins);
            String url = String.format("%s/vehicles/DecodeVINValuesBatch/%s?format=json", vpicBaseUrl, vinString);
            return fetchResults(url);
        } catch (Exception e) {
            log.severe("Error batch decoding VINs: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get parts associated with a recall
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getRecallParts(String campaignNumber) {
        try {
            String url = recallsBaseUrl + "/recalls/part?campaignNumber=" + campaignNumber;
            return fetchResults(url);
        } catch (Exception e) {
            log.severe("Error fetching recall parts for campaign: " + campaignNumber + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get recall chronology
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getRecallChronology(String campaignNumber) {
        try {
            String url = recallsBaseUrl + "/recalls/chronology?campaignNumber=" + campaignNumber;
            return fetchResults(url);
        } catch (Exception e) {
            log.severe("Error fetching recall chronology for campaign: " + campaignNumber + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Fetch results from NHTSA API with error handling
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchResults(String url) {
        try {
            log.info("Calling NHTSA API: " + url);
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            
            if (response.getBody() != null && response.getBody().has("Results")) {
                JsonNode resultsNode = response.getBody().get("Results");
                if (resultsNode.isArray()) {
                    return objectMapper.convertValue(resultsNode, List.class);
                } else if (resultsNode.isObject()) {
                    List<Map<String, Object>> list = new ArrayList<>();
                    list.add(objectMapper.convertValue(resultsNode, Map.class));
                    return list;
                }
            }
        } catch (Exception e) {
            log.severe("NHTSA API call failed: " + url + " - " + e.getMessage());
        }
        return new ArrayList<>();
    }
    
    /**
     * Check if the NHTSA API is reachable
     */
    public boolean isApiReachable() {
        try {
            String url = vpicBaseUrl + "/vehicles/GetAllMakes?format=json";
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.severe("NHTSA API is not reachable: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get API status information
     */
    public Map<String, Object> getApiStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("vpicBaseUrl", vpicBaseUrl);
        status.put("recallsBaseUrl", recallsBaseUrl);
        status.put("complaintsBaseUrl", complaintsBaseUrl);
        status.put("isReachable", isApiReachable());
        status.put("timeout", timeout);
        return status;
    }
}