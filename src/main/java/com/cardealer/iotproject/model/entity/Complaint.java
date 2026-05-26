package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhtsa_complaints")
public class Complaint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private Long complaintId;
    
    @Column(name = "odi_number", nullable = false, unique = true, length = 50)
    private String odiNumber;
    
    @Column(name = "date_complaint_received")
    private LocalDate dateComplaintReceived;
    
    @Column(name = "date_of_incident")
    private LocalDate dateOfIncident;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "make_id", nullable = false)
    private Make make;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;
    
    @Column(name = "model_year", nullable = false)
    private Integer modelYear;
    
    @Column(name = "component", nullable = false, length = 255)
    private String component;
    
    @Column(name = "complaint_text", nullable = false, columnDefinition = "TEXT")
    private String complaintText;
    
    @Column(name = "crash_flag")
    private Boolean crashFlag = false;
    
    @Column(name = "fire_flag")
    private Boolean fireFlag = false;
    
    @Column(name = "injuries_count")
    private Integer injuriesCount = 0;
    
    @Column(name = "deaths_count")
    private Integer deathsCount = 0;
    
    @Column(name = "manufacturer_response", columnDefinition = "TEXT")
    private String manufacturerResponse;
    
    @Column(name = "is_closed")
    private Boolean isClosed = false;
    
    @Column(name = "investigation_status", length = 100)
    private String investigationStatus;
    
    @Column(name = "recall_related")
    private Boolean recallRelated = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recall_id")
    private Recall recall;
    
    @Column(name = "sync_date")
    private LocalDateTime syncDate;
    
    public Complaint() {}
    
    @PrePersist
    protected void onCreate() {
        syncDate = LocalDateTime.now();
        if (injuriesCount == null) injuriesCount = 0;
        if (deathsCount == null) deathsCount = 0;
        if (crashFlag == null) crashFlag = false;
        if (fireFlag == null) fireFlag = false;
        if (isClosed == null) isClosed = false;
        if (recallRelated == null) recallRelated = false;
    }
    
    // Getters and Setters
    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
    
    public String getOdiNumber() { return odiNumber; }
    public void setOdiNumber(String odiNumber) { this.odiNumber = odiNumber; }
    
    public LocalDate getDateComplaintReceived() { return dateComplaintReceived; }
    public void setDateComplaintReceived(LocalDate dateComplaintReceived) { this.dateComplaintReceived = dateComplaintReceived; }
    
    public LocalDate getDateOfIncident() { return dateOfIncident; }
    public void setDateOfIncident(LocalDate dateOfIncident) { this.dateOfIncident = dateOfIncident; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    public Make getMake() { return make; }
    public void setMake(Make make) { this.make = make; }
    
    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }
    
    public Integer getModelYear() { return modelYear; }
    public void setModelYear(Integer modelYear) { this.modelYear = modelYear; }
    
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    
    public String getComplaintText() { return complaintText; }
    public void setComplaintText(String complaintText) { this.complaintText = complaintText; }
    
    public Boolean getCrashFlag() { return crashFlag; }
    public void setCrashFlag(Boolean crashFlag) { this.crashFlag = crashFlag; }
    
    public Boolean getFireFlag() { return fireFlag; }
    public void setFireFlag(Boolean fireFlag) { this.fireFlag = fireFlag; }
    
    public Integer getInjuriesCount() { return injuriesCount; }
    public void setInjuriesCount(Integer injuriesCount) { this.injuriesCount = injuriesCount; }
    
    public Integer getDeathsCount() { return deathsCount; }
    public void setDeathsCount(Integer deathsCount) { this.deathsCount = deathsCount; }
    
    public String getManufacturerResponse() { return manufacturerResponse; }
    public void setManufacturerResponse(String manufacturerResponse) { this.manufacturerResponse = manufacturerResponse; }
    
    public Boolean getIsClosed() { return isClosed; }
    public void setIsClosed(Boolean isClosed) { this.isClosed = isClosed; }
    
    public String getInvestigationStatus() { return investigationStatus; }
    public void setInvestigationStatus(String investigationStatus) { this.investigationStatus = investigationStatus; }
    
    public Boolean getRecallRelated() { return recallRelated; }
    public void setRecallRelated(Boolean recallRelated) { this.recallRelated = recallRelated; }
    
    public Recall getRecall() { return recall; }
    public void setRecall(Recall recall) { this.recall = recall; }
    
    public LocalDateTime getSyncDate() { return syncDate; }
    public void setSyncDate(LocalDateTime syncDate) { this.syncDate = syncDate; }
}