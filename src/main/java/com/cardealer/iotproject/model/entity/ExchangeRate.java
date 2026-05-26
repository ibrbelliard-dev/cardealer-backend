package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String currencyCode; // USD, EUR, GBP, etc.
    
    @Column(nullable = false)
    private String currencyName; // Dólar Americano, Euro, etc.
    
    @Column(nullable = false)
    private Double rateToDOP; // Tasa de cambio a pesos dominicanos
    
    @Column(nullable = false)
    private String symbol; // $, €, £, etc.
    
    @Column(nullable = false)
    private boolean isActive = true;
    
    private LocalDateTime updatedAt;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    public String getCurrencyName() {
        return currencyName;
    }
    
    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
    
    public Double getRateToDOP() {
        return rateToDOP;
    }
    
    public void setRateToDOP(Double rateToDOP) {
        this.rateToDOP = rateToDOP;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}