package com.cardealer.iotproject.model.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardStats {
    
    // Campos existentes...
    private long totalVehicles;
    private long availableVehicles;
    private long soldVehiclesThisMonth;
    private long vehiclesInService;
    private BigDecimal totalInventoryValue;
    private BigDecimal averageVehiclePrice;
    private BigDecimal monthlyRevenue;
    private Map<String, Object> recentSales;
    private Map<String, Object> topSellingMakes;
    private BigDecimal monthlyProfit;
    private long vehiclesSold;
    private long vehiclesSoldThisWeek;
    private long pendingVehicles;
    private long reservedVehicles;
    private long soldVehicles;
    private long openRecalls;
    private long recallsFixedThisMonth;
    private long affectedVehicles;
    private long totalComplaints;
    private long complaintsWithCrash;
    private long complaintsWithFire;
    private long totalTsbs;
    private List<Map<String, Object>> recentActivity;
    private List<Map<String, Object>> monthlySalesData;
    
    // NUEVOS CAMPOS DE COMISIONES
    private BigDecimal totalPendingCommissions;
    private BigDecimal totalPaidCommissions;
    private BigDecimal totalCommissionsThisMonth;
    private long pendingCommissionsCount;
    private long paidCommissionsCount;
    private List<Map<String, Object>> topPerformingSalesReps;
    private Map<String, Object> commissionSummary;
    
    // Default constructor
    public DashboardStats() {
    }
    
    // Getters y Setters nuevos
    public BigDecimal getTotalPendingCommissions() {
        return totalPendingCommissions;
    }
    
    public void setTotalPendingCommissions(BigDecimal totalPendingCommissions) {
        this.totalPendingCommissions = totalPendingCommissions;
    }
    
    public BigDecimal getTotalPaidCommissions() {
        return totalPaidCommissions;
    }
    
    public void setTotalPaidCommissions(BigDecimal totalPaidCommissions) {
        this.totalPaidCommissions = totalPaidCommissions;
    }
    
    public BigDecimal getTotalCommissionsThisMonth() {
        return totalCommissionsThisMonth;
    }
    
    public void setTotalCommissionsThisMonth(BigDecimal totalCommissionsThisMonth) {
        this.totalCommissionsThisMonth = totalCommissionsThisMonth;
    }
    
    public long getPendingCommissionsCount() {
        return pendingCommissionsCount;
    }
    
    public void setPendingCommissionsCount(long pendingCommissionsCount) {
        this.pendingCommissionsCount = pendingCommissionsCount;
    }
    
    public long getPaidCommissionsCount() {
        return paidCommissionsCount;
    }
    
    public void setPaidCommissionsCount(long paidCommissionsCount) {
        this.paidCommissionsCount = paidCommissionsCount;
    }
    
    public List<Map<String, Object>> getTopPerformingSalesReps() {
        return topPerformingSalesReps;
    }
    
    public void setTopPerformingSalesReps(List<Map<String, Object>> topPerformingSalesReps) {
        this.topPerformingSalesReps = topPerformingSalesReps;
    }
    
    public Map<String, Object> getCommissionSummary() {
        return commissionSummary;
    }
    
    public void setCommissionSummary(Map<String, Object> commissionSummary) {
        this.commissionSummary = commissionSummary;
    }
    
    // Getters existentes (mantener todos los que ya tenías)
    public long getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(long totalVehicles) { this.totalVehicles = totalVehicles; }
    
    public long getAvailableVehicles() { return availableVehicles; }
    public void setAvailableVehicles(long availableVehicles) { this.availableVehicles = availableVehicles; }
    
    public long getSoldVehiclesThisMonth() { return soldVehiclesThisMonth; }
    public void setSoldVehiclesThisMonth(long soldVehiclesThisMonth) { this.soldVehiclesThisMonth = soldVehiclesThisMonth; }
    
    public long getVehiclesInService() { return vehiclesInService; }
    public void setVehiclesInService(long vehiclesInService) { this.vehiclesInService = vehiclesInService; }
    
    public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }
    
    public BigDecimal getAverageVehiclePrice() { return averageVehiclePrice; }
    public void setAverageVehiclePrice(BigDecimal averageVehiclePrice) { this.averageVehiclePrice = averageVehiclePrice; }
    
    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    
    public Map<String, Object> getRecentSales() { return recentSales; }
    public void setRecentSales(Map<String, Object> recentSales) { this.recentSales = recentSales; }
    
    public Map<String, Object> getTopSellingMakes() { return topSellingMakes; }
    public void setTopSellingMakes(Map<String, Object> topSellingMakes) { this.topSellingMakes = topSellingMakes; }
    
    public BigDecimal getMonthlyProfit() { return monthlyProfit; }
    public void setMonthlyProfit(BigDecimal monthlyProfit) { this.monthlyProfit = monthlyProfit; }
    
    public long getVehiclesSold() { return vehiclesSold; }
    public void setVehiclesSold(long vehiclesSold) { this.vehiclesSold = vehiclesSold; }
    
    public long getVehiclesSoldThisWeek() { return vehiclesSoldThisWeek; }
    public void setVehiclesSoldThisWeek(long vehiclesSoldThisWeek) { this.vehiclesSoldThisWeek = vehiclesSoldThisWeek; }
    
    public long getPendingVehicles() { return pendingVehicles; }
    public void setPendingVehicles(long pendingVehicles) { this.pendingVehicles = pendingVehicles; }
    
    public long getReservedVehicles() { return reservedVehicles; }
    public void setReservedVehicles(long reservedVehicles) { this.reservedVehicles = reservedVehicles; }
    
    public long getSoldVehicles() { return soldVehicles; }
    public void setSoldVehicles(long soldVehicles) { this.soldVehicles = soldVehicles; }
    
    public long getOpenRecalls() { return openRecalls; }
    public void setOpenRecalls(long openRecalls) { this.openRecalls = openRecalls; }
    
    public long getRecallsFixedThisMonth() { return recallsFixedThisMonth; }
    public void setRecallsFixedThisMonth(long recallsFixedThisMonth) { this.recallsFixedThisMonth = recallsFixedThisMonth; }
    
    public long getAffectedVehicles() { return affectedVehicles; }
    public void setAffectedVehicles(long affectedVehicles) { this.affectedVehicles = affectedVehicles; }
    
    public long getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; }
    
    public long getComplaintsWithCrash() { return complaintsWithCrash; }
    public void setComplaintsWithCrash(long complaintsWithCrash) { this.complaintsWithCrash = complaintsWithCrash; }
    
    public long getComplaintsWithFire() { return complaintsWithFire; }
    public void setComplaintsWithFire(long complaintsWithFire) { this.complaintsWithFire = complaintsWithFire; }
    
    public long getTotalTsbs() { return totalTsbs; }
    public void setTotalTsbs(long totalTsbs) { this.totalTsbs = totalTsbs; }
    
    public List<Map<String, Object>> getRecentActivity() { return recentActivity; }
    public void setRecentActivity(List<Map<String, Object>> recentActivity) { this.recentActivity = recentActivity; }
    
    public List<Map<String, Object>> getMonthlySalesData() { return monthlySalesData; }
    public void setMonthlySalesData(List<Map<String, Object>> monthlySalesData) { this.monthlySalesData = monthlySalesData; }
    
    // Builder Pattern actualizado
    public static class Builder {
        // Campos existentes
        private long totalVehicles;
        private long availableVehicles;
        private long soldVehiclesThisMonth;
        private long vehiclesInService;
        private BigDecimal totalInventoryValue;
        private BigDecimal averageVehiclePrice;
        private BigDecimal monthlyRevenue;
        private Map<String, Object> recentSales;
        private Map<String, Object> topSellingMakes;
        private BigDecimal monthlyProfit;
        private long vehiclesSold;
        private long vehiclesSoldThisWeek;
        private long pendingVehicles;
        private long reservedVehicles;
        private long soldVehicles;
        private long openRecalls;
        private long recallsFixedThisMonth;
        private long affectedVehicles;
        private long totalComplaints;
        private long complaintsWithCrash;
        private long complaintsWithFire;
        private long totalTsbs;
        private List<Map<String, Object>> recentActivity;
        private List<Map<String, Object>> monthlySalesData;
        
        // Nuevos campos
        private BigDecimal totalPendingCommissions;
        private BigDecimal totalPaidCommissions;
        private BigDecimal totalCommissionsThisMonth;
        private long pendingCommissionsCount;
        private long paidCommissionsCount;
        private List<Map<String, Object>> topPerformingSalesReps;
        private Map<String, Object> commissionSummary;
        
        // Métodos existentes (mantener todos)
        public Builder totalVehicles(long totalVehicles) { this.totalVehicles = totalVehicles; return this; }
        public Builder availableVehicles(long availableVehicles) { this.availableVehicles = availableVehicles; return this; }
        public Builder soldVehiclesThisMonth(long soldVehiclesThisMonth) { this.soldVehiclesThisMonth = soldVehiclesThisMonth; return this; }
        public Builder vehiclesInService(long vehiclesInService) { this.vehiclesInService = vehiclesInService; return this; }
        public Builder totalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; return this; }
        public Builder averageVehiclePrice(BigDecimal averageVehiclePrice) { this.averageVehiclePrice = averageVehiclePrice; return this; }
        public Builder monthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; return this; }
        public Builder recentSales(Map<String, Object> recentSales) { this.recentSales = recentSales; return this; }
        public Builder topSellingMakes(Map<String, Object> topSellingMakes) { this.topSellingMakes = topSellingMakes; return this; }
        public Builder monthlyProfit(BigDecimal monthlyProfit) { this.monthlyProfit = monthlyProfit; return this; }
        public Builder vehiclesSold(long vehiclesSold) { this.vehiclesSold = vehiclesSold; return this; }
        public Builder vehiclesSoldThisWeek(long vehiclesSoldThisWeek) { this.vehiclesSoldThisWeek = vehiclesSoldThisWeek; return this; }
        public Builder pendingVehicles(long pendingVehicles) { this.pendingVehicles = pendingVehicles; return this; }
        public Builder reservedVehicles(long reservedVehicles) { this.reservedVehicles = reservedVehicles; return this; }
        public Builder soldVehicles(long soldVehicles) { this.soldVehicles = soldVehicles; return this; }
        public Builder openRecalls(long openRecalls) { this.openRecalls = openRecalls; return this; }
        public Builder recallsFixedThisMonth(long recallsFixedThisMonth) { this.recallsFixedThisMonth = recallsFixedThisMonth; return this; }
        public Builder affectedVehicles(long affectedVehicles) { this.affectedVehicles = affectedVehicles; return this; }
        public Builder totalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; return this; }
        public Builder complaintsWithCrash(long complaintsWithCrash) { this.complaintsWithCrash = complaintsWithCrash; return this; }
        public Builder complaintsWithFire(long complaintsWithFire) { this.complaintsWithFire = complaintsWithFire; return this; }
        public Builder totalTsbs(long totalTsbs) { this.totalTsbs = totalTsbs; return this; }
        public Builder recentActivity(List<Map<String, Object>> recentActivity) { this.recentActivity = recentActivity; return this; }
        public Builder monthlySalesData(List<Map<String, Object>> monthlySalesData) { this.monthlySalesData = monthlySalesData; return this; }
        
        // Nuevos métodos
        public Builder totalPendingCommissions(BigDecimal totalPendingCommissions) { 
            this.totalPendingCommissions = totalPendingCommissions; 
            return this; 
        }
        
        public Builder totalPaidCommissions(BigDecimal totalPaidCommissions) { 
            this.totalPaidCommissions = totalPaidCommissions; 
            return this; 
        }
        
        public Builder totalCommissionsThisMonth(BigDecimal totalCommissionsThisMonth) { 
            this.totalCommissionsThisMonth = totalCommissionsThisMonth; 
            return this; 
        }
        
        public Builder pendingCommissionsCount(long pendingCommissionsCount) { 
            this.pendingCommissionsCount = pendingCommissionsCount; 
            return this; 
        }
        
        public Builder paidCommissionsCount(long paidCommissionsCount) { 
            this.paidCommissionsCount = paidCommissionsCount; 
            return this; 
        }
        
        public Builder topPerformingSalesReps(List<Map<String, Object>> topPerformingSalesReps) { 
            this.topPerformingSalesReps = topPerformingSalesReps; 
            return this; 
        }
        
        public Builder commissionSummary(Map<String, Object> commissionSummary) { 
            this.commissionSummary = commissionSummary; 
            return this; 
        }
        
        public DashboardStats build() {
            DashboardStats stats = new DashboardStats();
            // Campos existentes
            stats.totalVehicles = this.totalVehicles;
            stats.availableVehicles = this.availableVehicles;
            stats.soldVehiclesThisMonth = this.soldVehiclesThisMonth;
            stats.vehiclesInService = this.vehiclesInService;
            stats.totalInventoryValue = this.totalInventoryValue;
            stats.averageVehiclePrice = this.averageVehiclePrice;
            stats.monthlyRevenue = this.monthlyRevenue;
            stats.recentSales = this.recentSales;
            stats.topSellingMakes = this.topSellingMakes;
            stats.monthlyProfit = this.monthlyProfit;
            stats.vehiclesSold = this.vehiclesSold;
            stats.vehiclesSoldThisWeek = this.vehiclesSoldThisWeek;
            stats.pendingVehicles = this.pendingVehicles;
            stats.reservedVehicles = this.reservedVehicles;
            stats.soldVehicles = this.soldVehicles;
            stats.openRecalls = this.openRecalls;
            stats.recallsFixedThisMonth = this.recallsFixedThisMonth;
            stats.affectedVehicles = this.affectedVehicles;
            stats.totalComplaints = this.totalComplaints;
            stats.complaintsWithCrash = this.complaintsWithCrash;
            stats.complaintsWithFire = this.complaintsWithFire;
            stats.totalTsbs = this.totalTsbs;
            stats.recentActivity = this.recentActivity;
            stats.monthlySalesData = this.monthlySalesData;
            
            // Nuevos campos
            stats.totalPendingCommissions = this.totalPendingCommissions;
            stats.totalPaidCommissions = this.totalPaidCommissions;
            stats.totalCommissionsThisMonth = this.totalCommissionsThisMonth;
            stats.pendingCommissionsCount = this.pendingCommissionsCount;
            stats.paidCommissionsCount = this.paidCommissionsCount;
            stats.topPerformingSalesReps = this.topPerformingSalesReps;
            stats.commissionSummary = this.commissionSummary;
            
            return stats;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}