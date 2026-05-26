package com.cardealer.iotproject.dto;

import java.time.LocalDateTime;


public class ProductDTO {
    private Integer id;
    private String title;
    private String codigo;
    private Integer catid;
    private String description;
    private String bigimgpath;
    private String smallimgpath;
    private Double price1;
    private Double price2;
    private Integer qtyoh;
    private String location;
    private Integer status;
    private Integer materialid;
    private LocalDateTime createddate;
    
    // Constructors
    public ProductDTO() {
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public Integer getCatid() {
        return catid;
    }
    
    public void setCatid(Integer catid) {
        this.catid = catid;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getBigimgpath() {
        return bigimgpath;
    }
    
    public void setBigimgpath(String bigimgpath) {
        this.bigimgpath = bigimgpath;
    }
    
    public String getSmallimgpath() {
        return smallimgpath;
    }
    
    public void setSmallimgpath(String smallimgpath) {
        this.smallimgpath = smallimgpath;
    }
    
    public Double getPrice1() {
        return price1;
    }
    
    public void setPrice1(Double price1) {
        this.price1 = price1;
    }
    
    public Double getPrice2() {
        return price2;
    }
    
    public void setPrice2(Double price2) {
        this.price2 = price2;
    }
    
    public Integer getQtyoh() {
        return qtyoh;
    }
    
    public void setQtyoh(Integer qtyoh) {
        this.qtyoh = qtyoh;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getMaterialid() {
        return materialid;
    }
    
    public void setMaterialid(Integer materialid) {
        this.materialid = materialid;
    }
    
    public LocalDateTime getCreateddate() {
        return createddate;
    }
    
    public void setCreateddate(LocalDateTime createddate) {
        this.createddate = createddate;
    }
}