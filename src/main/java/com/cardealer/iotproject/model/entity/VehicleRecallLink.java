package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle_recall_links")
public class VehicleRecallLink {
    
    @EmbeddedId
    private VehicleRecallLinkId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vehicleId")
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recallId")
    @JoinColumn(name = "recall_id")
    private Recall recall;
    
    @Column(name = "repaired_flag")
    private Boolean repairedFlag = false;
    
    @Column(name = "repair_date")
    private LocalDate repairDate;
    
    @Column(name = "repair_notes", columnDefinition = "TEXT")
    private String repairNotes;
    
    @Column(name = "notification_date")
    private LocalDate notificationDate;
    
    @Column(name = "customer_notified")
    private Boolean customerNotified = false;
    
    public VehicleRecallLink() {}
    
    // Getters and Setters
    public VehicleRecallLinkId getId() {
        return id;
    }
    
    public void setId(VehicleRecallLinkId id) {
        this.id = id;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    public Recall getRecall() {
        return recall;
    }
    
    public void setRecall(Recall recall) {
        this.recall = recall;
    }
    
    public Boolean getRepairedFlag() {
        return repairedFlag;
    }
    
    public void setRepairedFlag(Boolean repairedFlag) {
        this.repairedFlag = repairedFlag;
    }
    
    public LocalDate getRepairDate() {
        return repairDate;
    }
    
    public void setRepairDate(LocalDate repairDate) {
        this.repairDate = repairDate;
    }
    
    public String getRepairNotes() {
        return repairNotes;
    }
    
    public void setRepairNotes(String repairNotes) {
        this.repairNotes = repairNotes;
    }
    
    public LocalDate getNotificationDate() {
        return notificationDate;
    }
    
    public void setNotificationDate(LocalDate notificationDate) {
        this.notificationDate = notificationDate;
    }
    
    public Boolean getCustomerNotified() {
        return customerNotified;
    }
    
    public void setCustomerNotified(Boolean customerNotified) {
        this.customerNotified = customerNotified;
    }
}