package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Vehicle;
import com.cardealer.iotproject.model.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    /**
     * Find vehicle by VIN number
     */
    Optional<Vehicle> findByVin(String vin);
    
    /**
     * Find vehicles by status
     */
    List<Vehicle> findByStatus(VehicleStatus status);
    
    /**
     * Search vehicles with filters
     */
    @Query("SELECT v FROM Vehicle v WHERE " +
           "(:make IS NULL OR LOWER(v.make.makeName) LIKE LOWER(CONCAT('%', :make, '%'))) AND " +
           "(:model IS NULL OR LOWER(v.model.modelName) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(:yearMin IS NULL OR v.modelYear >= :yearMin) AND " +
           "(:yearMax IS NULL OR v.modelYear <= :yearMax) AND " +
           "(:status IS NULL OR v.status = :status)")
    Page<Vehicle> searchVehicles(@Param("make") String make,
                                 @Param("model") String model,
                                 @Param("yearMin") Integer yearMin,
                                 @Param("yearMax") Integer yearMax,
                                 @Param("status") VehicleStatus status,
                                 Pageable pageable);
    
    /**
     * Get inventory statistics (total count, total value, average price)
     */
    @Query("SELECT COUNT(v), COALESCE(SUM(v.purchasePrice), 0), COALESCE(AVG(v.purchasePrice), 0) FROM Vehicle v WHERE v.status = 'AVAILABLE' AND v.isActive = true")
    List<Object[]> getInventoryStats();
    
    /**
     * Get monthly sales statistics
     */
    @Query("SELECT COUNT(v), COALESCE(SUM(v.sellingPrice), 0) FROM Vehicle v WHERE v.status = 'SOLD' AND MONTH(v.lastModified) = MONTH(CURRENT_DATE) AND YEAR(v.lastModified) = YEAR(CURRENT_DATE)")
    List<Object[]> getMonthlySalesStats();
    
    /**
     * Find vehicles by selling price range
     */
    @Query("SELECT v FROM Vehicle v WHERE v.sellingPrice BETWEEN :minPrice AND :maxPrice")
    List<Vehicle> findBySellingPriceBetween(@Param("minPrice") BigDecimal minPrice, 
                                            @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Find vehicle by ID with make and model eagerly loaded
     */
    @Query("SELECT v FROM Vehicle v LEFT JOIN FETCH v.make LEFT JOIN FETCH v.model WHERE v.vehicleId = :vehicleId")
    Optional<Vehicle> findByIdWithMakeAndModel(@Param("vehicleId") Long vehicleId);
    
    /**
     * Count available vehicles
     */
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.status = 'AVAILABLE' AND v.isActive = true")
    long countAvailableVehicles();
    
    /**
     * Count sold vehicles
     */
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.status = 'SOLD'")
    long countSoldVehicles();
    
    /**
     * Count vehicles in service
     */
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.status = 'SERVICE'")
    long countVehiclesInService();
    
    /**
     * Get vehicles by make name
     */
    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.make.makeName) = LOWER(:makeName)")
    List<Vehicle> findByMakeName(@Param("makeName") String makeName);
    
    /**
     * Get vehicles by model name
     */
    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.model.modelName) = LOWER(:modelName)")
    List<Vehicle> findByModelName(@Param("modelName") String modelName);
    
    /**
     * Get recent vehicles (last 30 days) - CORRECTED VERSION
     */
    @Query("SELECT v FROM Vehicle v WHERE v.dateAdded >= :date ORDER BY v.dateAdded DESC")
    List<Vehicle> findRecentVehicles(@Param("date") java.time.LocalDateTime date);
    
    /**
     * Search vehicles by VIN (partial match)
     */
    @Query("SELECT v FROM Vehicle v WHERE v.vin LIKE CONCAT('%', :vin, '%')")
    List<Vehicle> searchByVin(@Param("vin") String vin);
    
    /**
     * Get total inventory value
     */
    @Query("SELECT COALESCE(SUM(v.purchasePrice), 0) FROM Vehicle v WHERE v.status = 'AVAILABLE' AND v.isActive = true")
    BigDecimal getTotalInventoryValue();
    
    
    /**
     * Get vehicles that need service (based on mileage)
     */
    @Query("SELECT v FROM Vehicle v WHERE v.mileage > 50000 AND v.status = 'AVAILABLE'")
    List<Vehicle> findVehiclesNeedingService();
    
    /**
     * Get vehicle count by make
     */
    @Query("SELECT v.make.makeName, COUNT(v) FROM Vehicle v GROUP BY v.make.makeName ORDER BY COUNT(v) DESC")
    List<Object[]> getVehicleCountByMake();
}