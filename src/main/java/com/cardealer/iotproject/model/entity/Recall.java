package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhtsa_recalls")
public class Recall {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recall_id")
    private Long recallId;
    
    @Column(name = "nhtsa_campaign_number", nullable = false, unique = true, length = 20)
    private String nhtsaCampaignNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;
    
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
    
    @Column(name = "component", nullable = false, length = 255)
    private String component;
    
    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "consequence", columnDefinition = "TEXT")
    private String consequence;
    
    @Column(name = "remedy", columnDefinition = "TEXT")
    private String remedy;
    
    @Column(name = "report_received_date")
    private LocalDate reportReceivedDate;
    
    @Column(name = "recall_status", length = 100)
    private String recallStatus;
    
    @Column(name = "manufacturer_notes", columnDefinition = "TEXT")
    private String manufacturerNotes;
    
    @Column(name = "date_added_to_file")
    private LocalDateTime dateAddedToFile;
    
    public Recall() {}
    
    // Getters and Setters
    public Long getRecallId() { return recallId; }
    public void setRecallId(Long recallId) { this.recallId = recallId; }
    
    public String getNhtsaCampaignNumber() { return nhtsaCampaignNumber; }
    public void setNhtsaCampaignNumber(String nhtsaCampaignNumber) { this.nhtsaCampaignNumber = nhtsaCampaignNumber; }
    
    public Manufacturer getManufacturer() { return manufacturer; }
    public void setManufacturer(Manufacturer manufacturer) { this.manufacturer = manufacturer; }
    
    public Make getMake() { return make; }
    public void setMake(Make make) { this.make = make; }
    
    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }
    
    public Integer getModelYearFrom() { return modelYearFrom; }
    public void setModelYearFrom(Integer modelYearFrom) { this.modelYearFrom = modelYearFrom; }
    
    public Integer getModelYearTo() { return modelYearTo; }
    public void setModelYearTo(Integer modelYearTo) { this.modelYearTo = modelYearTo; }
    
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getConsequence() { return consequence; }
    public void setConsequence(String consequence) { this.consequence = consequence; }
    
    public String getRemedy() { return remedy; }
    public void setRemedy(String remedy) { this.remedy = remedy; }
    
    public LocalDate getReportReceivedDate() { return reportReceivedDate; }
    public void setReportReceivedDate(LocalDate reportReceivedDate) { this.reportReceivedDate = reportReceivedDate; }
    
    public String getRecallStatus() { return recallStatus; }
    public void setRecallStatus(String recallStatus) { this.recallStatus = recallStatus; }
    
    public String getManufacturerNotes() { return manufacturerNotes; }
    public void setManufacturerNotes(String manufacturerNotes) { this.manufacturerNotes = manufacturerNotes; }
    
    public LocalDateTime getDateAddedToFile() { return dateAddedToFile; }
    public void setDateAddedToFile(LocalDateTime dateAddedToFile) { this.dateAddedToFile = dateAddedToFile; }
}