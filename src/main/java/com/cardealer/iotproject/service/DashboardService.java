package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.dto.DashboardStats;
import com.cardealer.iotproject.model.entity.Vehicle;
import com.cardealer.iotproject.model.enums.VehicleStatus;
import com.cardealer.iotproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private MakeRepository makeRepository;
    
    @Autowired
    private ModelRepository modelRepository;
    
    @Autowired
    private RecallRepository recallRepository;
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private TSBRepository tsbRepository;
    
    @Autowired
    private VehicleRecallLinkRepository vehicleRecallLinkRepository;
    
    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        
        long totalVehicles = vehicleRepository.count();
        long availableVehicles = vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).size();
        long soldVehicles = vehicleRepository.findByStatus(VehicleStatus.SOLD).size();
        long vehiclesInService = vehicleRepository.findByStatus(VehicleStatus.SERVICE).size();
        long pendingVehicles = vehicleRepository.findByStatus(VehicleStatus.PENDING).size();
        long reservedVehicles = vehicleRepository.findByStatus(VehicleStatus.RESERVED).size();
        
        BigDecimal totalInventoryValue = calculateTotalInventoryValue();
        BigDecimal averageVehiclePrice = calculateAverageVehiclePrice();
        BigDecimal monthlyRevenue = calculateMonthlyRevenue(startOfMonth, endOfMonth);
        BigDecimal monthlyProfit = calculateMonthlyProfit(startOfMonth, endOfMonth);
        
        long vehiclesSoldThisMonth = getVehiclesSoldCount(startOfMonth, endOfMonth);
        long vehiclesSoldThisWeek = getVehiclesSoldCount(
            LocalDateTime.now().minusDays(7), 
            LocalDateTime.now()
        );
        
        long openRecalls = recallRepository.count();
        long affectedVehicles = getAffectedVehiclesCount();
        
        long totalComplaints = complaintRepository.count();
        long complaintsWithCrash = complaintRepository.findByCrashFlagTrue(org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        long complaintsWithFire = complaintRepository.findByFireFlagTrue(org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        
        long totalTsbs = tsbRepository.count();
        
        List<Map<String, Object>> recentActivity = getRecentActivity(10);
        Map<String, Object> topSellingMakes = getTopSellingMakes();
        List<Map<String, Object>> monthlySalesData = getMonthlySalesData();
        
        return DashboardStats.builder()
            .totalVehicles(totalVehicles)
            .availableVehicles(availableVehicles)
            .soldVehiclesThisMonth(vehiclesSoldThisMonth)
            .vehiclesInService(vehiclesInService)
            .totalInventoryValue(totalInventoryValue)
            .averageVehiclePrice(averageVehiclePrice)
            .monthlyRevenue(monthlyRevenue)
            .monthlyProfit(monthlyProfit)
            .vehiclesSold(vehiclesSoldThisMonth)
            .vehiclesSoldThisWeek(vehiclesSoldThisWeek)
            .pendingVehicles(pendingVehicles)
            .reservedVehicles(reservedVehicles)
            .soldVehicles(soldVehicles)
            .openRecalls(openRecalls)
            .affectedVehicles(affectedVehicles)
            .totalComplaints(totalComplaints)
            .complaintsWithCrash(complaintsWithCrash)
            .complaintsWithFire(complaintsWithFire)
            .totalTsbs(totalTsbs)
            .recentActivity(recentActivity)
            .recentSales(getRecentSalesData())
            .topSellingMakes(topSellingMakes)
            .monthlySalesData(monthlySalesData)
            .build();
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRecentActivity(int limit) {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        try {
            // Get recently added vehicles with null safety
            List<Vehicle> recentVehicles = vehicleRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, limit, 
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "dateAdded"))
            ).getContent();
            
            for (Vehicle vehicle : recentVehicles) {
                if (vehicle.getDateAdded() != null) {
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("type", "VEHICLE_ADDED");
                    activity.put("message", "Nuevo vehículo agregado: " + 
                        (vehicle.getModelYear() != null ? vehicle.getModelYear() : "?") + " " + 
                        (vehicle.getMake() != null && vehicle.getMake().getMakeName() != null ? vehicle.getMake().getMakeName() : "Unknown") + " " + 
                        (vehicle.getModel() != null && vehicle.getModel().getModelName() != null ? vehicle.getModel().getModelName() : "Unknown"));
                    activity.put("timestamp", vehicle.getDateAdded());
                    activity.put("vehicleId", vehicle.getVehicleId());
                    activity.put("color", "success");
                    activity.put("icon", "DirectionsCar");
                    activities.add(activity);
                }
            }
            
            // Get recently sold vehicles with null safety
            List<Vehicle> soldVehicles = vehicleRepository.findByStatus(VehicleStatus.SOLD);
            for (Vehicle vehicle : soldVehicles) {
                if (vehicle.getLastModified() != null) {
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("type", "VEHICLE_SOLD");
                    activity.put("message", "Vehículo vendido: " + 
                        (vehicle.getModelYear() != null ? vehicle.getModelYear() : "?") + " " + 
                        (vehicle.getMake() != null && vehicle.getMake().getMakeName() != null ? vehicle.getMake().getMakeName() : "Unknown") + " " + 
                        (vehicle.getModel() != null && vehicle.getModel().getModelName() != null ? vehicle.getModel().getModelName() : "Unknown") + 
                        " por " + (vehicle.getSellingPrice() != null ? formatCurrency(vehicle.getSellingPrice()) : "N/A"));
                    activity.put("timestamp", vehicle.getLastModified());
                    activity.put("vehicleId", vehicle.getVehicleId());
                    activity.put("color", "primary");
                    activity.put("icon", "PointOfSale");
                    activities.add(activity);
                }
            }
            
            // Sort safely - filter out null timestamps first
            activities = activities.stream()
                .filter(a -> a.get("timestamp") != null)
                .sorted((a, b) -> {
                    LocalDateTime timeA = (LocalDateTime) a.get("timestamp");
                    LocalDateTime timeB = (LocalDateTime) b.get("timestamp");
                    if (timeA == null && timeB == null) return 0;
                    if (timeA == null) return 1;
                    if (timeB == null) return -1;
                    return timeB.compareTo(timeA);
                })
                .limit(limit)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error getting recent activity: " + e.getMessage());
            return new ArrayList<>();
        }
        
        return activities;
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlySalesData() {
        List<Map<String, Object>> salesData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 11; i >= 0; i--) {
            LocalDateTime startOfMonth = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            
            long vehiclesSold = getVehiclesSoldCount(startOfMonth, endOfMonth);
            BigDecimal revenue = calculateMonthlyRevenue(startOfMonth, endOfMonth);
            BigDecimal profit = calculateMonthlyProfit(startOfMonth, endOfMonth);
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", startOfMonth.getMonth().toString());
            monthData.put("year", startOfMonth.getYear());
            monthData.put("vehiclesSold", vehiclesSold);
            monthData.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
            monthData.put("profit", profit != null ? profit : BigDecimal.ZERO);
            
            salesData.add(monthData);
        }
        
        return salesData;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getTopSellingMakes() {
        List<Vehicle> soldVehicles = vehicleRepository.findByStatus(VehicleStatus.SOLD);
        
        Map<String, Long> salesByMake = new HashMap<>();
        for (Vehicle vehicle : soldVehicles) {
            String makeName = vehicle.getMake() != null && vehicle.getMake().getMakeName() != null ? 
                vehicle.getMake().getMakeName() : "Unknown";
            salesByMake.put(makeName, salesByMake.getOrDefault(makeName, 0L) + 1);
        }
        
        List<Map<String, Object>> topMakes = salesByMake.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(entry -> {
                Map<String, Object> makeData = new HashMap<>();
                makeData.put("make", entry.getKey());
                makeData.put("count", entry.getValue());
                return makeData;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("topMakes", topMakes);
        result.put("salesByMake", salesByMake);
        
        salesByMake.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .ifPresent(entry -> {
                result.put("topMake", entry.getKey());
                result.put("topMakeSales", entry.getValue());
            });
        
        return result;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getRecentSalesData() {
        Map<String, Object> recentSales = new HashMap<>();
        List<Vehicle> soldVehicles = vehicleRepository.findByStatus(VehicleStatus.SOLD)
            .stream()
            .filter(v -> v.getLastModified() != null)
            .sorted((v1, v2) -> {
                if (v1.getLastModified() == null && v2.getLastModified() == null) return 0;
                if (v1.getLastModified() == null) return 1;
                if (v2.getLastModified() == null) return -1;
                return v2.getLastModified().compareTo(v1.getLastModified());
            })
            .limit(5)
            .collect(Collectors.toList());
        
        BigDecimal totalRevenue = soldVehicles.stream()
            .map(Vehicle::getSellingPrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        recentSales.put("count", soldVehicles.size());
        recentSales.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        recentSales.put("vehicles", soldVehicles);
        recentSales.put("averagePrice", soldVehicles.isEmpty() ? BigDecimal.ZERO : 
            totalRevenue.divide(BigDecimal.valueOf(soldVehicles.size()), 2, RoundingMode.HALF_UP));
        
        return recentSales;
    }
    
    private BigDecimal calculateTotalInventoryValue() {
        List<Vehicle> availableVehicles = vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);
        return availableVehicles.stream()
            .map(Vehicle::getPurchasePrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calculateAverageVehiclePrice() {
        List<Vehicle> vehicles = vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);
        if (vehicles.isEmpty()) return BigDecimal.ZERO;
        
        BigDecimal total = vehicles.stream()
            .map(Vehicle::getPurchasePrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long count = vehicles.stream().filter(v -> v.getPurchasePrice() != null).count();
        if (count == 0) return BigDecimal.ZERO;
        
        return total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateMonthlyRevenue(LocalDateTime start, LocalDateTime end) {
        List<Vehicle> soldVehicles = vehicleRepository.findByStatus(VehicleStatus.SOLD);
        return soldVehicles.stream()
            .filter(v -> v.getLastModified() != null && 
                        v.getLastModified().isAfter(start) && 
                        v.getLastModified().isBefore(end))
            .map(Vehicle::getSellingPrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calculateMonthlyProfit(LocalDateTime start, LocalDateTime end) {
        List<Vehicle> soldVehicles = vehicleRepository.findByStatus(VehicleStatus.SOLD);
        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal cost = BigDecimal.ZERO;
        
        for (Vehicle vehicle : soldVehicles) {
            if (vehicle.getLastModified() != null && 
                vehicle.getLastModified().isAfter(start) && 
                vehicle.getLastModified().isBefore(end)) {
                if (vehicle.getSellingPrice() != null) {
                    revenue = revenue.add(vehicle.getSellingPrice());
                }
                if (vehicle.getPurchasePrice() != null) {
                    cost = cost.add(vehicle.getPurchasePrice());
                }
            }
        }
        
        return revenue.subtract(cost);
    }
    
    private long getVehiclesSoldCount(LocalDateTime start, LocalDateTime end) {
        List<Vehicle> soldVehicles = vehicleRepository.findByStatus(VehicleStatus.SOLD);
        return soldVehicles.stream()
            .filter(v -> v.getLastModified() != null && 
                        v.getLastModified().isAfter(start) && 
                        v.getLastModified().isBefore(end))
            .count();
    }
    
    private long getAffectedVehiclesCount() {
        try {
            return vehicleRecallLinkRepository.findAll().stream()
                .filter(vrl -> vrl.getRepairedFlag() != null && !vrl.getRepairedFlag())
                .count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private String formatCurrency(BigDecimal value) {
        if (value == null) return "$0";
        return String.format("$%,.0f", value);
    }
}