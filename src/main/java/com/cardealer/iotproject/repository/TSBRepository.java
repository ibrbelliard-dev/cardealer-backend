package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.ManufacturerCommunication;
import com.cardealer.iotproject.model.enums.CommType;
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
public interface TSBRepository extends JpaRepository<ManufacturerCommunication, Long> {
    
    /**
     * Find TSB by NHTSA ID number
     */
    Optional<ManufacturerCommunication> findByNhtsaIdNumber(Integer nhtsaIdNumber);
    
    /**
     * Find TSB by document ID
     */
    Optional<ManufacturerCommunication> findByTsbDocumentId(String tsbDocumentId);
    
    /**
     * Find TSBs by make ID
     */
    List<ManufacturerCommunication> findByMake_MakeId(Integer makeId);
    
    /**
     * Find TSBs by make and model
     */
    List<ManufacturerCommunication> findByMake_MakeIdAndModel_ModelId(Integer makeId, Integer modelId);
    
    /**
     * Find TSBs by communication type
     */
    List<ManufacturerCommunication> findByCommunicationType(CommType commType);
    
    /**
     * Find TSBs by component system
     */
    List<ManufacturerCommunication> findByMfrComponentSystemContainingIgnoreCase(String system);
    
    /**
     * Find TSBs for a specific vehicle (by make, model, and year)
     */
    @Query("SELECT t FROM ManufacturerCommunication t WHERE " +
           "(t.make.makeId = :makeId OR t.make IS NULL) AND " +
           "(t.model.modelId = :modelId OR t.model IS NULL) AND " +
           "(t.modelYearFrom <= :year OR t.modelYearFrom IS NULL) AND " +
           "(t.modelYearTo >= :year OR t.modelYearTo IS NULL)")
    List<ManufacturerCommunication> findTsbsForVehicle(@Param("makeId") Integer makeId,
                                                        @Param("modelId") Integer modelId,
                                                        @Param("year") Integer year);
    
    /**
     * Find TSBs by date range
     */
    List<ManufacturerCommunication> findByMfrCommunicationDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find recent TSBs
     */
    @Query("SELECT t FROM ManufacturerCommunication t ORDER BY t.mfrCommunicationDate DESC")
    Page<ManufacturerCommunication> findRecentTsbs(Pageable pageable);
    
    /**
     * Search TSBs by summary text
     */
    @Query("SELECT t FROM ManufacturerCommunication t WHERE " +
           "LOWER(t.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.nhtsaComponents) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.mfrComponentSystem) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ManufacturerCommunication> searchTsbs(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Get TSB count by component system
     */
    @Query("SELECT t.mfrComponentSystem, COUNT(t) FROM ManufacturerCommunication t " +
           "WHERE t.mfrComponentSystem IS NOT NULL GROUP BY t.mfrComponentSystem ORDER BY COUNT(t) DESC")
    List<Object[]> getTsbCountByComponentSystem();
    
    /**
     * Get TSB count by communication type
     */
    @Query("SELECT t.communicationType, COUNT(t) FROM ManufacturerCommunication t GROUP BY t.communicationType")
    List<Object[]> getTsbCountByType();
    
    /**
     * Get TSB count by make
     */
    @Query("SELECT t.make.makeName, COUNT(t) FROM ManufacturerCommunication t " +
           "WHERE t.make IS NOT NULL GROUP BY t.make.makeName ORDER BY COUNT(t) DESC")
    List<Object[]> getTsbCountByMake();
    
    /**
     * Find TSBs without make/model association (generic TSBs)
     */
    @Query("SELECT t FROM ManufacturerCommunication t WHERE t.make IS NULL OR t.model IS NULL")
    List<ManufacturerCommunication> findGenericTsbs();
    
    /**
     * Find TSBs by model year range
     */
    List<ManufacturerCommunication> findByModelYearFromLessThanEqualAndModelYearToGreaterThanEqual(
        Integer yearFrom, Integer yearTo);
    
    /**
     * Get TSB statistics by year
     */
    @Query("SELECT YEAR(t.mfrCommunicationDate), COUNT(t) FROM ManufacturerCommunication t " +
           "WHERE t.mfrCommunicationDate IS NOT NULL GROUP BY YEAR(t.mfrCommunicationDate) ORDER BY YEAR(t.mfrCommunicationDate) DESC")
    List<Object[]> getTsbCountByYear();
    
    /**
     * Find TSBs that match a specific component
     */
    @Query("SELECT t FROM ManufacturerCommunication t WHERE " +
           "LOWER(t.nhtsaComponents) LIKE LOWER(CONCAT('%', :component, '%'))")
    List<ManufacturerCommunication> findByComponent(@Param("component") String component);
    
    /**
     * Get total TSB count for a specific make
     */
    long countByMake_MakeId(Integer makeId);
    
    /**
     * Get total TSB count for a specific model
     */
    long countByModel_ModelId(Integer modelId);
    
    /**
     * Find TSBs by internal campaign ID
     */
    Optional<ManufacturerCommunication> findByMfrInternalCampaignId(String internalCampaignId);
    
    /**
     * Get latest TSBs by date (limited)
     */
    @Query("SELECT t FROM ManufacturerCommunication t ORDER BY t.mfrCommunicationDate DESC")
    List<ManufacturerCommunication> findLatestTsbs(Pageable pageable);
    
    /**
     * Get TSB summary statistics
     */
    @Query("SELECT new map(" +
           "COUNT(t) as totalTsbs, " +
           "COUNT(DISTINCT t.make) as affectedMakes, " +
           "COUNT(DISTINCT t.model) as affectedModels, " +
           "MAX(t.mfrCommunicationDate) as latestTsbDate, " +
           "MIN(t.mfrCommunicationDate) as earliestTsbDate) " +
           "FROM ManufacturerCommunication t")
    List<Object[]> getTsbSummaryStatistics();
    
    /**
     * Check if TSB exists by NHTSA ID
     */
    boolean existsByNhtsaIdNumber(Integer nhtsaIdNumber);
    
    /**
     * Delete TSBs older than specified date
     */
    @Query("DELETE FROM ManufacturerCommunication t WHERE t.mfrCommunicationDate < :date")
    void deleteOldTsbs(@Param("date") LocalDate date);
}