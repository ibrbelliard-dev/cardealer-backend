package com.cardealer.iotproject.dto;

public class LocationDTO {
    private Integer id;
    private String nombre;
    private String address;
    private String title;
    
    public LocationDTO() {}
    
    public LocationDTO(Integer id, String nombre, String address, String title) {
        this.id = id;
        this.nombre = nombre;
        this.address = address;
        this.title = title;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
}
