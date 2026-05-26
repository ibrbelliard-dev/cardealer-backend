package com.cardealer.iotproject.model.entity;

import com.cardealer.iotproject.model.enums.VehicleCondition;
import com.cardealer.iotproject.model.enums.VehicleStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nhtsa_vehicles")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;
    
    @Column(name = "vin", length = 17, unique = true)
    private String vin;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "make_id", nullable = false)
    private Make make;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;
    
    @Column(name = "model_year", nullable = false)
    private Integer modelYear;
    
    @Column(name = "vin_decoded_json", columnDefinition = "TEXT")
    private String vinDecodedJson;
    
    @Column(name = "body_class", length = 100)
    private String bodyClass;
    
    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;
    
    @Column(name = "drive_type", length = 20)
    private String driveType;
    
    @Column(name = "engine_cylinders")
    private Integer engineCylinders;
    
    @Column(name = "engine_displacement_cc")
    private Integer engineDisplacementCc;
    
    @Column(name = "engine_displacement_l")
    private BigDecimal engineDisplacementL;
    
    @Column(name = "engine_horsepower")
    private Integer engineHorsepower;
    
    @Column(name = "fuel_type_primary", length = 50)
    private String fuelTypePrimary;
    
    @Column(name = "fuel_type_secondary", length = 50)
    private String fuelTypeSecondary;
    
    @Column(name = "transmission_style", length = 100)
    private String transmissionStyle;
    
    @Column(name = "transmission_speeds")
    private Integer transmissionSpeeds;
    
    @Column(name = "doors")
    private Integer doors;
    
    @Column(name = "windows")
    private Integer windows;
    
    @Column(name = "plant_city", length = 100)
    private String plantCity;
    
    @Column(name = "plant_state", length = 50)
    private String plantState;
    
    @Column(name = "plant_country", length = 100)
    private String plantCountry;
    
    @Column(name = "series", length = 100)
    private String series;
    
    @Column(name = "trim", length = 100)
    private String trim;
    
    @Column(name = "vehicle_curb_weight_lbs")
    private Integer vehicleCurbWeightLbs;
    
    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;
    
    @Column(name = "selling_price")
    private BigDecimal sellingPrice;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "mileage")
    private Integer mileage;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_condition")
    private VehicleCondition condition;
    
    @Column(name = "color", length = 50)
    private String color;
    
    @Column(name = "interior_color", length = 50)
    private String interiorColor;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VehicleStatus status;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VehicleImage> images = new ArrayList<>();
    
    @Column(name = "date_added")
    private LocalDateTime dateAdded;
    
    @Column(name = "last_modified")
    private LocalDateTime lastModified;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    // En Vehicle.java, agrega este campo:
    @Column(name = "mileage_unit", length = 10)
     private String mileageUnit; // "KM" o "MILLAS"
    
    public Vehicle() {}
    
// Agrega getter y setter:
    public String getMileageUnit() { return mileageUnit; }
    public void setMileageUnit(String mileageUnit) { this.mileageUnit = mileageUnit; }

    // Getters and Setters (all)
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    
    public Make getMake() { return make; }
    public void setMake(Make make) { this.make = make; }
    
    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }
    
    public Integer getModelYear() { return modelYear; }
    public void setModelYear(Integer modelYear) { this.modelYear = modelYear; }
    
    public String getVinDecodedJson() { return vinDecodedJson; }
    public void setVinDecodedJson(String vinDecodedJson) { this.vinDecodedJson = vinDecodedJson; }
    
    public String getBodyClass() { return bodyClass; }
    public void setBodyClass(String bodyClass) { this.bodyClass = bodyClass; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public String getDriveType() { return driveType; }
    public void setDriveType(String driveType) { this.driveType = driveType; }
    
    public Integer getEngineCylinders() { return engineCylinders; }
    public void setEngineCylinders(Integer engineCylinders) { this.engineCylinders = engineCylinders; }
    
    public Integer getEngineDisplacementCc() { return engineDisplacementCc; }
    public void setEngineDisplacementCc(Integer engineDisplacementCc) { this.engineDisplacementCc = engineDisplacementCc; }
    
    public BigDecimal getEngineDisplacementL() { return engineDisplacementL; }
    public void setEngineDisplacementL(BigDecimal engineDisplacementL) { this.engineDisplacementL = engineDisplacementL; }
    
    public Integer getEngineHorsepower() { return engineHorsepower; }
    public void setEngineHorsepower(Integer engineHorsepower) { this.engineHorsepower = engineHorsepower; }
    
    public String getFuelTypePrimary() { return fuelTypePrimary; }
    public void setFuelTypePrimary(String fuelTypePrimary) { this.fuelTypePrimary = fuelTypePrimary; }
    
    public String getFuelTypeSecondary() { return fuelTypeSecondary; }
    public void setFuelTypeSecondary(String fuelTypeSecondary) { this.fuelTypeSecondary = fuelTypeSecondary; }
    
    public String getTransmissionStyle() { return transmissionStyle; }
    public void setTransmissionStyle(String transmissionStyle) { this.transmissionStyle = transmissionStyle; }
    
    public Integer getTransmissionSpeeds() { return transmissionSpeeds; }
    public void setTransmissionSpeeds(Integer transmissionSpeeds) { this.transmissionSpeeds = transmissionSpeeds; }
    
    public Integer getDoors() { return doors; }
    public void setDoors(Integer doors) { this.doors = doors; }
    
    public Integer getWindows() { return windows; }
    public void setWindows(Integer windows) { this.windows = windows; }
    
    public String getPlantCity() { return plantCity; }
    public void setPlantCity(String plantCity) { this.plantCity = plantCity; }
    
    public String getPlantState() { return plantState; }
    public void setPlantState(String plantState) { this.plantState = plantState; }
    
    public String getPlantCountry() { return plantCountry; }
    public void setPlantCountry(String plantCountry) { this.plantCountry = plantCountry; }
    
    
    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
    
    public String getTrim() { return trim; }
    public void setTrim(String trim) { this.trim = trim; }
    
    public Integer getVehicleCurbWeightLbs() { return vehicleCurbWeightLbs; }
    public void setVehicleCurbWeightLbs(Integer vehicleCurbWeightLbs) { this.vehicleCurbWeightLbs = vehicleCurbWeightLbs; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    
    public VehicleCondition getCondition() { return condition; }
    public void setCondition(VehicleCondition condition) { this.condition = condition; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getInteriorColor() { return interiorColor; }
    public void setInteriorColor(String interiorColor) { this.interiorColor = interiorColor; }
    
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<VehicleImage> getImages() { return images; }
    public void setImages(List<VehicleImage> images) { this.images = images; }
    
    public LocalDateTime getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}