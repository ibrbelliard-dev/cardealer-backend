package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.WMI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WMIRepository extends JpaRepository<WMI, Long> {
    
    /**
     * Find WMI by WMI code
     */
    Optional<WMI> findByWmi(String wmi);
    
    /**
     * Find WMIs by manufacturer name (case insensitive)
     */
    List<WMI> findByManufacturerNameContainingIgnoreCase(String manufacturerName);
    
    /**
     * Find WMIs by plant country
     */
    List<WMI> findByPlantCountry(String country);
    
    /**
     * Find active WMIs
     */
    List<WMI> findByIsActiveTrue();
    
    /**
     * Find WMIs by vehicle type
     */
    List<WMI> findByVehicleType(String vehicleType);
    
    /**
     * Find WMIs by region (based on first character)
     */
    @Query("SELECT w FROM WMI w WHERE SUBSTRING(w.wmi, 1, 1) = :regionCode")
    List<WMI> findByWmiRegion(@Param("regionCode") String regionCode);
    
    /**
     * Count WMIs by country
     */
    @Query("SELECT w.plantCountry, COUNT(w) FROM WMI w WHERE w.plantCountry IS NOT NULL GROUP BY w.plantCountry ORDER BY COUNT(w) DESC")
    List<Object[]> countWmiByCountry();
    
    /**
     * Count WMIs by manufacturer
     */
    @Query("SELECT w.manufacturerName, COUNT(w) FROM WMI w WHERE w.manufacturerName IS NOT NULL GROUP BY w.manufacturerName ORDER BY COUNT(w) DESC")
    List<Object[]> countWmiByManufacturer();
    
    /**
     * Find WMIs by date range
     */
    List<WMI> findByDateCreatedBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find active WMIs for a specific manufacturer
     */
    @Query("SELECT w FROM WMI w WHERE w.manufacturerName = :manufacturerName AND w.isActive = true")
    List<WMI> findActiveWmiByManufacturer(@Param("manufacturerName") String manufacturerName);
    
    /**
     * Get WMI statistics
     */
    @Query("SELECT COUNT(w) as total, " +
           "COUNT(DISTINCT w.manufacturerName) as uniqueManufacturers, " +
           "COUNT(DISTINCT w.plantCountry) as countries " +
           "FROM WMI w WHERE w.isActive = true")
    List<Object[]> getWmiStatistics();
    
    /**
     * Search WMIs by any field
     */
    @Query("SELECT w FROM WMI w WHERE " +
           "LOWER(w.wmi) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(w.manufacturerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(w.plantCountry) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<WMI> searchWmi(@Param("searchTerm") String searchTerm);
    
    /**
     * Check if WMI exists
     */
    boolean existsByWmi(String wmi);
}