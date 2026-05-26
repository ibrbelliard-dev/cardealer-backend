package com.cardealer.iotproject.dto;

import java.time.LocalDateTime;

public class UserDTO {
    private Integer id;
    private String name;
    private String lastname;
    private String email;
    private String code;
    private String secretword;
    private String locid;
    private String cell;
    private Integer type;
    private Integer status;
    private LocalDateTime createddate;  // Added this field
    
    // Constructors
    public UserDTO() {}
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getSecretword() { return secretword; }
    public void setSecretword(String secretword) { this.secretword = secretword; }
    
    public String getLocid() { return locid; }
    public void setLocid(String locid) { this.locid = locid; }
    
    public String getCell() { return cell; }
    public void setCell(String cell) { this.cell = cell; }
    
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public LocalDateTime getCreateddate() { return createddate; }
    public void setCreateddate(LocalDateTime createddate) { this.createddate = createddate; }
}