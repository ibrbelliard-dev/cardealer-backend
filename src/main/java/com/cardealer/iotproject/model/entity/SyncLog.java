package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhtsa_sync_log")
public class SyncLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sync_id")
    private Long syncId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_type", nullable = false)
    private SyncType syncType;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "records_processed")
    private Integer recordsProcessed = 0;
    
    @Column(name = "records_added")
    private Integer recordsAdded = 0;
    
    @Column(name = "records_updated")
    private Integer recordsUpdated = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SyncStatus status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    public SyncLog() {}
    
    // Getters and Setters
    public Long getSyncId() { return syncId; }
    public void setSyncId(Long syncId) { this.syncId = syncId; }
    
    public SyncType getSyncType() { return syncType; }
    public void setSyncType(SyncType syncType) { this.syncType = syncType; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Integer getRecordsProcessed() { return recordsProcessed; }
    public void setRecordsProcessed(Integer recordsProcessed) { this.recordsProcessed = recordsProcessed; }
    
    public Integer getRecordsAdded() { return recordsAdded; }
    public void setRecordsAdded(Integer recordsAdded) { this.recordsAdded = recordsAdded; }
    
    public Integer getRecordsUpdated() { return recordsUpdated; }
    public void setRecordsUpdated(Integer recordsUpdated) { this.recordsUpdated = recordsUpdated; }
    
    public SyncStatus getStatus() { return status; }
    public void setStatus(SyncStatus status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public enum SyncType {
        MAKES, MODELS, MANUFACTURERS, WMI, RECALLS, TSB, COMPLAINTS, VARIABLES, PLANTS, FULL
    }
    
    public enum SyncStatus {
        RUNNING, COMPLETED, FAILED
    }
}