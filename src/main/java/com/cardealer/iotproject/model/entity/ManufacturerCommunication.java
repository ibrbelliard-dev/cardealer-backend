package com.cardealer.iotproject.model.entity;

import com.cardealer.iotproject.model.enums.CommType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhtsa_mfr_comms")
public class ManufacturerCommunication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comm_id")
    private Long commId;
    
    @Column(name = "nhtsa_id_number", nullable = false, unique = true)
    private Integer nhtsaIdNumber;
    
    @Column(name = "tsb_document_id", length = 128)
    private String tsbDocumentId;
    
    @Column(name = "replacement_tsb_number", length = 16)
    private String replacementTsbNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "communication_type", nullable = false)
    private CommType communicationType;
    
    @Column(name = "mfr_communication_date")
    private LocalDate mfrCommunicationDate;
    
    @Column(name = "date_added_to_file")
    private LocalDate dateAddedToFile;
    
    @Column(name = "mfr_internal_campaign_id", length = 128)
    private String mfrInternalCampaignId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "make_id")
    private Make make;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;
    
    @Column(name = "model_year_from")
    private Integer modelYearFrom;
    
    @Column(name = "model_year_to")
    private Integer modelYearTo;
    
    @Column(name = "nhtsa_components", length = 256)
    private String nhtsaComponents;
    
    @Column(name = "mfr_component_system", length = 256)
    private String mfrComponentSystem;
    
    @Column(name = "mfr_component_subsystem", length = 256)
    private String mfrComponentSubsystem;
    
    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "document_url", length = 500)
    private String documentUrl;
    
    @Column(name = "has_pdf")
    private Boolean hasPdf = false;
    
    @Column(name = "labor_hours")
    private Double laborHours;
    
    @Column(name = "parts_required", columnDefinition = "TEXT")
    private String partsRequired;
    
    @Column(name = "diagnostic_information", columnDefinition = "TEXT")
    private String diagnosticInformation;
    
    @Column(name = "repair_procedure", columnDefinition = "TEXT")
    private String repairProcedure;
    
    @Column(name = "sync_date")
    private LocalDateTime syncDate;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    public ManufacturerCommunication() {}
    
    // Getters and Setters
    public Long getCommId() { return commId; }
    public void setCommId(Long commId) { this.commId = commId; }
    
    public Integer getNhtsaIdNumber() { return nhtsaIdNumber; }
    public void setNhtsaIdNumber(Integer nhtsaIdNumber) { this.nhtsaIdNumber = nhtsaIdNumber; }
    
    public String getTsbDocumentId() { return tsbDocumentId; }
    public void setTsbDocumentId(String tsbDocumentId) { this.tsbDocumentId = tsbDocumentId; }
    
    public String getReplacementTsbNumber() { return replacementTsbNumber; }
    public void setReplacementTsbNumber(String replacementTsbNumber) { this.replacementTsbNumber = replacementTsbNumber; }
    
    public CommType getCommunicationType() { return communicationType; }
    public void setCommunicationType(CommType communicationType) { this.communicationType = communicationType; }
    
    public LocalDate getMfrCommunicationDate() { return mfrCommunicationDate; }
    public void setMfrCommunicationDate(LocalDate mfrCommunicationDate) { this.mfrCommunicationDate = mfrCommunicationDate; }
    
    public LocalDate getDateAddedToFile() { return dateAddedToFile; }
    public void setDateAddedToFile(LocalDate dateAddedToFile) { this.dateAddedToFile = dateAddedToFile; }
    
    public String getMfrInternalCampaignId() { return mfrInternalCampaignId; }
    public void setMfrInternalCampaignId(String mfrInternalCampaignId) { this.mfrInternalCampaignId = mfrInternalCampaignId; }
    
    public Make getMake() { return make; }
    public void setMake(Make make) { this.make = make; }
    
    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }
    
    public Integer getModelYearFrom() { return modelYearFrom; }
    public void setModelYearFrom(Integer modelYearFrom) { this.modelYearFrom = modelYearFrom; }
    
    public Integer getModelYearTo() { return modelYearTo; }
    public void setModelYearTo(Integer modelYearTo) { this.modelYearTo = modelYearTo; }
    
    public String getNhtsaComponents() { return nhtsaComponents; }
    public void setNhtsaComponents(String nhtsaComponents) { this.nhtsaComponents = nhtsaComponents; }
    
    public String getMfrComponentSystem() { return mfrComponentSystem; }
    public void setMfrComponentSystem(String mfrComponentSystem) { this.mfrComponentSystem = mfrComponentSystem; }
    
    public String getMfrComponentSubsystem() { return mfrComponentSubsystem; }
    public void setMfrComponentSubsystem(String mfrComponentSubsystem) { this.mfrComponentSubsystem = mfrComponentSubsystem; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    
    public Boolean getHasPdf() { return hasPdf; }
    public void setHasPdf(Boolean hasPdf) { this.hasPdf = hasPdf; }
    
    public Double getLaborHours() { return laborHours; }
    public void setLaborHours(Double laborHours) { this.laborHours = laborHours; }
    
    public String getPartsRequired() { return partsRequired; }
    public void setPartsRequired(String partsRequired) { this.partsRequired = partsRequired; }
    
    public String getDiagnosticInformation() { return diagnosticInformation; }
    public void setDiagnosticInformation(String diagnosticInformation) { this.diagnosticInformation = diagnosticInformation; }
    
    public String getRepairProcedure() { return repairProcedure; }
    public void setRepairProcedure(String repairProcedure) { this.repairProcedure = repairProcedure; }
    
    public LocalDateTime getSyncDate() { return syncDate; }
    public void setSyncDate(LocalDateTime syncDate) { this.syncDate = syncDate; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}