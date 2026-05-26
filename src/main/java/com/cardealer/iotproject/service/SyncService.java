package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.*;
import com.cardealer.iotproject.model.enums.CommType;
import com.cardealer.iotproject.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class SyncService {
    
    private static final Logger log = Logger.getLogger(SyncService.class.getName());
    
    @Autowired
    private NhtsaService nhtsaService;
    
    @Autowired
    private MakeRepository makeRepository;
    
    @Autowired
    private ModelRepository modelRepository;
    
    @Autowired
    private TSBRepository tsbRepository;
    
    @Autowired
    private SyncLogRepository syncLogRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Async
    @Transactional
    public CompletableFuture<Integer> syncAllData() {
        SyncLog syncLog = new SyncLog();
        syncLog.setSyncType(SyncLog.SyncType.FULL);
        syncLog.setStartTime(LocalDateTime.now());
        syncLog.setStatus(SyncLog.SyncStatus.RUNNING);
        syncLogRepository.save(syncLog);
        
        int totalRecords = 0;
        
        try {
            // Sync makes
            totalRecords += nhtsaService.syncAllMakes();
            
            // Sync manufacturer communications (TSBs)
            totalRecords += syncTsbData();
            
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setStatus(SyncLog.SyncStatus.COMPLETED);
            syncLog.setRecordsProcessed(totalRecords);
            syncLogRepository.save(syncLog);
            
            log.info("Full sync completed: " + totalRecords + " records processed");
        } catch (Exception e) {
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setStatus(SyncLog.SyncStatus.FAILED);
            syncLog.setErrorMessage(e.getMessage());
            syncLogRepository.save(syncLog);
            log.severe("Sync failed: " + e.getMessage());
        }
        
        return CompletableFuture.completedFuture(totalRecords);
    }
    
    @Async
    @Transactional
    public CompletableFuture<Integer> syncMakesOnly() {
        SyncLog syncLog = new SyncLog();
        syncLog.setSyncType(SyncLog.SyncType.MAKES);
        syncLog.setStartTime(LocalDateTime.now());
        syncLog.setStatus(SyncLog.SyncStatus.RUNNING);
        syncLogRepository.save(syncLog);
        
        int count = 0;
        try {
            count = nhtsaService.syncAllMakes();
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setStatus(SyncLog.SyncStatus.COMPLETED);
            syncLog.setRecordsProcessed(count);
            syncLogRepository.save(syncLog);
            log.info("Makes sync completed: " + count + " records");
        } catch (Exception e) {
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setStatus(SyncLog.SyncStatus.FAILED);
            syncLog.setErrorMessage(e.getMessage());
            syncLogRepository.save(syncLog);
            log.severe("Makes sync failed: " + e.getMessage());
        }
        
        return CompletableFuture.completedFuture(count);
    }
    
    @Async
    @Transactional
    public CompletableFuture<Integer> syncModelsForMake(String makeName) {
        SyncLog syncLog = new SyncLog();
        syncLog.setSyncType(SyncLog.SyncType.MODELS);
        syncLog.setStartTime(LocalDateTime.now());
        syncLog.setStatus(SyncLog.SyncStatus.RUNNING);
        syncLogRepository.save(syncLog);
        
        int count = 0;
        try {
            count = nhtsaService.syncModelsForMake(makeName);
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setStatus(SyncLog.SyncStatus.COMPLETED);
            syncLog.setRecordsProcessed(count);
            syncLogRepository.save(syncLog);
            log.info("Models sync for " + makeName + " completed: " + count + " records");
        } catch (Exception e) {
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setStatus(SyncLog.SyncStatus.FAILED);
            syncLog.setErrorMessage(e.getMessage());
            syncLogRepository.save(syncLog);
            log.severe("Models sync failed: " + e.getMessage());
        }
        
        return CompletableFuture.completedFuture(count);
    }
    
    @Transactional
    public int syncTsbData() {
        int count = 0;
        String tsbFileUrl = "https://static.nhtsa.gov/nhtsa/downloads/Temp/MfrComms.txt";
        
        try {
            URL url = new URL(tsbFileUrl);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String line;
                boolean isFirstLine = true;
                
                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    
                    String[] fields = line.split("\t");
                    if (fields.length < 14) continue;
                    
                    try {
                        ManufacturerCommunication comm = new ManufacturerCommunication();
                        
                        // Set basic fields
                        comm.setNhtsaIdNumber(Integer.parseInt(fields[0].trim()));
                        
                        if (fields[1].trim().isEmpty()) {
                            comm.setReplacementTsbNumber(null);
                        } else {
                            comm.setReplacementTsbNumber(fields[1].trim());
                        }
                        
                        comm.setDateAddedToFile(parseDate(fields[2].trim()));
                        
                        if (fields[3].trim().isEmpty()) {
                            comm.setTsbDocumentId(null);
                        } else {
                            comm.setTsbDocumentId(fields[3].trim());
                        }
                        
                        comm.setMfrCommunicationDate(parseDate(fields[4].trim()));
                        
                        if (fields[5].trim().isEmpty()) {
                            comm.setMfrInternalCampaignId(null);
                        } else {
                            comm.setMfrInternalCampaignId(fields[5].trim());
                        }
                        
                        // Set communication type
                        try {
                            comm.setCommunicationType(CommType.valueOf(fields[6].trim()));
                        } catch (IllegalArgumentException e) {
                            comm.setCommunicationType(CommType.OTH);
                        }
                        
                        // Set components
                        if (fields[10].trim().isEmpty()) {
                            comm.setNhtsaComponents(null);
                        } else {
                            comm.setNhtsaComponents(fields[10].trim());
                        }
                        
                        if (fields[11].trim().isEmpty()) {
                            comm.setMfrComponentSystem(null);
                        } else {
                            comm.setMfrComponentSystem(fields[11].trim());
                        }
                        
                        if (fields[12].trim().isEmpty()) {
                            comm.setMfrComponentSubsystem(null);
                        } else {
                            comm.setMfrComponentSubsystem(fields[12].trim());
                        }
                        
                        comm.setSummary(fields[13].trim());
                        comm.setSyncDate(LocalDateTime.now());
                        
                        // Set make if available
                        String makeName = fields[7].trim();
                        if (!makeName.isEmpty()) {
                            Optional<Make> makeOpt = makeRepository.findByMakeName(makeName);
                            makeOpt.ifPresent(comm::setMake);
                        }
                        
                        // Set model if available
                        String modelName = fields[8].trim();
                        if (!modelName.isEmpty() && comm.getMake() != null) {
                            Optional<Model> modelOpt = modelRepository.findByMakeAndModelName(comm.getMake(), modelName);
                            modelOpt.ifPresent(comm::setModel);
                        }
                        
                        // Parse model year range
                        String yearRange = fields[9].trim();
                        if (!yearRange.isEmpty() && !yearRange.equals("9999")) {
                            if (yearRange.contains("-")) {
                                String[] years = yearRange.split("-");
                                comm.setModelYearFrom(Integer.parseInt(years[0]));
                                comm.setModelYearTo(Integer.parseInt(years[1]));
                            } else {
                                int year = Integer.parseInt(yearRange);
                                comm.setModelYearFrom(year);
                                comm.setModelYearTo(year);
                            }
                        }
                        
                        tsbRepository.save(comm);
                        count++;
                        
                        if (count % 1000 == 0) {
                            log.info("Processed " + count + " TSB records...");
                        }
                        
                    } catch (Exception e) {
                        log.warning("Error parsing TSB record at line " + (count + 1) + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.severe("Failed to sync TSB data: " + e.getMessage());
            throw new RuntimeException("TSB sync failed", e);
        }
        
        log.info("Synced " + count + " TSB records");
        return count;
    }
    
    @Async
    @Transactional
    public CompletableFuture<Integer> syncTsbDataAsync() {
        int count = syncTsbData();
        return CompletableFuture.completedFuture(count);
    }
    
    @Transactional(readOnly = true)
    public SyncLog getLastSyncLog(SyncLog.SyncType syncType) {
        return syncLogRepository.findTopBySyncTypeOrderByStartTimeDesc(syncType);
    }
    
    @Transactional(readOnly = true)
    public List<SyncLog> getSyncLogs(SyncLog.SyncType syncType, int limit) {
        return syncLogRepository.findBySyncTypeAndStatus(syncType, null)
            .stream()
            .limit(limit)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public boolean isSyncInProgress() {
        List<SyncLog> runningSyncs = syncLogRepository.findBySyncTypeAndStatus(
            SyncLog.SyncType.FULL, SyncLog.SyncStatus.RUNNING);
        return !runningSyncs.isEmpty();
    }
    
    /**
     * Parse date string from NHTSA format (YYYYMMDD)
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.length() < 8) {
            return null;
        }
        try {
            int year = Integer.parseInt(dateStr.substring(0, 4));
            int month = Integer.parseInt(dateStr.substring(4, 6));
            int day = Integer.parseInt(dateStr.substring(6, 8));
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            log.warning("Failed to parse date: " + dateStr);
            return null;
        }
    }
}