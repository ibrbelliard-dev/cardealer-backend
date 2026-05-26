package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    /**
     * Find complaint by ODI number
     */
    Optional<Complaint> findByOdiNumber(String odiNumber);
    
    /**
     * Find complaints by vehicle ID
     */
    List<Complaint> findByVehicle_VehicleId(Long vehicleId);
    
    /**
     * Find complaints by make, model, and year
     */
    List<Complaint> findByMake_MakeIdAndModel_ModelIdAndModelYear(
        Integer makeId, Integer modelId, Integer modelYear);
    
    /**
     * Find complaints by make name
     */
    List<Complaint> findByMake_MakeName(String makeName);
    
    /**
     * Find complaints by component
     */
    List<Complaint> findByComponentContainingIgnoreCase(String component);
    
    /**
     * Find complaints with crash flag
     */
    Page<Complaint> findByCrashFlagTrue(Pageable pageable);
    
    /**
     * Find complaints with fire flag
     */
    Page<Complaint> findByFireFlagTrue(Pageable pageable);
    
    /**
     * Find complaints with injuries
     */
    List<Complaint> findByInjuriesCountGreaterThan(int count);
    
    /**
     * Find complaints with fatalities
     */
    List<Complaint> findByDeathsCountGreaterThan(int count);
    
    /**
     * Find complaints by date range
     */
    List<Complaint> findByDateComplaintReceivedBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find complaints by date of incident
     */
    List<Complaint> findByDateOfIncidentAfter(LocalDate date);
    
    /**
     * Get top complaint components
     */
    @Query("SELECT c.component, COUNT(c) FROM Complaint c GROUP BY c.component ORDER BY COUNT(c) DESC")
    List<Object[]> getTopComplaintComponents();
    
    /**
     * Get complaints count by make
     */
    @Query("SELECT c.make.makeName, COUNT(c) FROM Complaint c GROUP BY c.make.makeName ORDER BY COUNT(c) DESC")
    List<Object[]> getComplaintCountByMake();
    
    /**
     * Get complaints count by year
     */
    @Query("SELECT YEAR(c.dateComplaintReceived), COUNT(c) FROM Complaint c WHERE c.dateComplaintReceived IS NOT NULL GROUP BY YEAR(c.dateComplaintReceived)")
    List<Object[]> getComplaintCountByYear();
    
    /**
     * Get complaints with crashes by make
     */
    @Query("SELECT c.make.makeName, COUNT(c) FROM Complaint c WHERE c.crashFlag = true GROUP BY c.make.makeName ORDER BY COUNT(c) DESC")
    List<Object[]> getCrashComplaintsByMake();
    
    /**
     * Get complaints with fatalities
     */
    @Query("SELECT c FROM Complaint c WHERE c.deathsCount > 0 ORDER BY c.deathsCount DESC")
    List<Complaint> findComplaintsWithFatalities();
    
    /**
     * Search complaints by text
     */
    @Query("SELECT c FROM Complaint c WHERE " +
           "LOWER(c.complaintText) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.component) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.odiNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Complaint> searchComplaints(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Get recent complaints
     */
    @Query("SELECT c FROM Complaint c ORDER BY c.dateComplaintReceived DESC")
    Page<Complaint> findRecentComplaints(Pageable pageable);
    
    /**
     * Get complaint statistics by component
     */
    @Query("SELECT c.component, COUNT(c), SUM(c.injuriesCount), SUM(c.deathsCount) " +
           "FROM Complaint c GROUP BY c.component ORDER BY COUNT(c) DESC")
    List<Object[]> getComponentStatistics();
    
    /**
     * Check if complaint exists by ODI number
     */
    boolean existsByOdiNumber(String odiNumber);
    
    /**
     * Get total injuries count
     */
    @Query("SELECT COALESCE(SUM(c.injuriesCount), 0) FROM Complaint c")
    long getTotalInjuries();
    
    /**
     * Get total fatalities count
     */
    @Query("SELECT COALESCE(SUM(c.deathsCount), 0) FROM Complaint c")
    long getTotalFatalities();
    
    /**
     * Get complaints by severity (injury + fatality)
     */
    @Query("SELECT c FROM Complaint c WHERE c.injuriesCount > 0 OR c.deathsCount > 0 ORDER BY (c.injuriesCount + c.deathsCount * 10) DESC")
    List<Complaint> findSevereComplaints(Pageable pageable);
    
    /**
     * Get complaints related to recalls
     */
    List<Complaint> findByRecallRelatedTrue();
    
    /**
     * Get complaints by recall ID
     */
    List<Complaint> findByRecall_RecallId(Long recallId);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.vehicle.vehicleId = :vehicleId")
    long countByVehicle_VehicleId(@Param("vehicleId") Long vehicleId);
}