package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    
    Optional<Manufacturer> findByNhtsaManufacturerId(Integer nhtsaManufacturerId);
    
    Optional<Manufacturer> findByManufacturerNameIgnoreCase(String manufacturerName);
    
    List<Manufacturer> findByManufacturerType(String manufacturerType);
    
    List<Manufacturer> findByCountry(String country);
    
    Optional<Manufacturer> findByPrimaryWmi(String wmi);
    
    List<Manufacturer> findByManufacturerNameContainingIgnoreCase(String name);
    
    @Query("SELECT m FROM Manufacturer m WHERE m.contactPhone IS NOT NULL OR m.contactEmail IS NOT NULL")
    List<Manufacturer> findManufacturersWithContactInfo();
    
    @Query("SELECT m.country, COUNT(m) FROM Manufacturer m GROUP BY m.country ORDER BY COUNT(m) DESC")
    List<Object[]> countManufacturersByCountry();
    
    // Removed the problematic findActiveManufacturers() method
    
    @Query("SELECT COUNT(m) FROM Manufacturer m")
    long getTotalManufacturerCount();
}