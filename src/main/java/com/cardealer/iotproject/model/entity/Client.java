package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_clients", indexes = {
    @Index(name = "idx_cedula", columnList = "cedula"),
    @Index(name = "idx_rnc", columnList = "rnc"),
    @Index(name = "idx_firstname", columnList = "firstname"),
    @Index(name = "idx_lastname", columnList = "lastname"),
    @Index(name = "idx_tipo", columnList = "tipo"),
    @Index(name = "idx_email_addr", columnList = "email_addr")
})
public class Client {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "cedula", unique = true, length = 11)
    private String cedula;
    
    @Column(name = "firstname", length = 50)
    private String firstname;
    
    @Column(name = "lastname", length = 50)
    private String lastname;
    
    @Column(name = "rnc", unique = true, length = 20)
    private String rnc;
    
    @Column(name = "cell", length = 20)
    private String cell;
    
    @Column(name = "business_phone", length = 15)
    private String businessPhone;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "ciudad", length = 255)
    private String ciudad;
    
    @Column(name = "provincia", length = 255)
    private String provincia;
    
    @Column(name = "empresa", length = 255)
    private String empresa;
    
    @Column(name = "contact", length = 255)
    private String contact;
    
    @Column(name = "email_addr", length = 150)  // Add this field
    private String emailAddr;
    
    @Column(name = "status")
    private Integer status = 1;
    
    @Column(name = "tipo")
    private Integer tipo;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Client() {}
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = 1;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    
    public String getRnc() { return rnc; }
    public void setRnc(String rnc) { this.rnc = rnc; }
    
    public String getCell() { return cell; }
    public void setCell(String cell) { this.cell = cell; }
    
    public String getBusinessPhone() { return businessPhone; }
    public void setBusinessPhone(String businessPhone) { this.businessPhone = businessPhone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getEmailAddr() { return emailAddr; }  // Add getter
    public void setEmailAddr(String emailAddr) { this.emailAddr = emailAddr; }  // Add setter
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public Integer getTipo() { return tipo; }
    public void setTipo(Integer tipo) { this.tipo = tipo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public String getFullName() {
        if (tipo != null && tipo == 1) {
            return empresa != null ? empresa : "";
        }
        return (firstname != null ? firstname : "") + " " + (lastname != null ? lastname : "");
    }
    
    public String getTipoLabel() {
        return tipo != null && tipo == 1 ? "Empresa" : "Individuo";
    }
    
    public String getStatusLabel() {
        return status != null && status == 1 ? "Activo" : "Inactivo";
    }
    
    public boolean isBusiness() {
        return tipo != null && tipo == 1;
    }
    
    public boolean isActive() {
        return status != null && status == 1;
    }
}