package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_info")
public class Company {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;
    
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;
    
    @Column(name = "rnc", length = 20)
    private String rnc;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "provincia", length = 100)
    private String provincia;
    
    @Column(name = "zip_code", length = 20)
    private String zipCode;
    
    @Column(name = "phone", length = 50)
    private String phone;
    
    @Column(name = "cell", length = 50)
    private String cell;
    
    @Column(name = "contact_person", length = 100)
    private String contact;
    
    @Column(name = "email_addr", length = 150)
    private String emailAddr;
    
    @Column(name = "website", length = 200)
    private String website;
    
    @Column(name = "application_title", length = 200)
    private String applicationTitle;
    
    @Column(name = "logo_path", length = 500)
    private String logoPath;
    
    // New fields
    @Column(name = "aboutus", columnDefinition = "TEXT")
    private String aboutus;
    
    @Column(name = "ourmission", columnDefinition = "TEXT")
    private String ourmission;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Company() {}
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getRnc() { return rnc; }
    public void setRnc(String rnc) { this.rnc = rnc; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getCell() { return cell; }
    public void setCell(String cell) { this.cell = cell; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getEmailAddr() { return emailAddr; }
    public void setEmailAddr(String emailAddr) { this.emailAddr = emailAddr; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public String getApplicationTitle() { return applicationTitle; }
    public void setApplicationTitle(String applicationTitle) { this.applicationTitle = applicationTitle; }
    
    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
    
    public String getAboutus() { return aboutus; }
    public void setAboutus(String aboutus) { this.aboutus = aboutus; }
    
    public String getOurmission() { return ourmission; }
    public void setOurmission(String ourmission) { this.ourmission = ourmission; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}