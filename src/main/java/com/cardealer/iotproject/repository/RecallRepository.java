package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Recall;
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
public interface RecallRepository extends JpaRepository<Recall, Long> {
    
    Optional<Recall> findByNhtsaCampaignNumber(String campaignNumber);
    
    List<Recall> findByMake_MakeId(Integer makeId);
    
    List<Recall> findByMake_MakeIdAndModel_ModelId(Integer makeId, Integer modelId);
    
    List<Recall> findByComponentContainingIgnoreCase(String component);
    
    @Query("SELECT r FROM Recall r WHERE " +
           "(r.make.makeId = :makeId OR :makeId IS NULL) AND " +
           "(r.model.modelId = :modelId OR :modelId IS NULL) AND " +
           "(r.modelYearFrom <= :year OR r.modelYearFrom IS NULL) AND " +
           "(r.modelYearTo >= :year OR r.modelYearTo IS NULL)")
    List<Recall> findRecallsForVehicle(@Param("makeId") Integer makeId,
                                       @Param("modelId") Integer modelId,
                                       @Param("year") Integer year);
    
    @Query("SELECT r FROM Recall r WHERE " +
           "(:makeName IS NULL OR LOWER(r.make.makeName) LIKE LOWER(CONCAT('%', :makeName, '%'))) AND " +
           "(:modelName IS NULL OR LOWER(r.model.modelName) LIKE LOWER(CONCAT('%', :modelName, '%'))) AND " +
           "(:year IS NULL OR (:year BETWEEN r.modelYearFrom AND r.modelYearTo))")
    Page<Recall> searchRecalls(@Param("makeName") String makeName,
                               @Param("modelName") String modelName,
                               @Param("year") Integer year,
                               Pageable pageable);
    
    @Query("SELECT r.component, COUNT(r) FROM Recall r GROUP BY r.component ORDER BY COUNT(r) DESC")
    List<Object[]> getRecallCountByComponent();
    
    @Query("SELECT YEAR(r.reportReceivedDate), COUNT(r) FROM Recall r WHERE r.reportReceivedDate IS NOT NULL GROUP BY YEAR(r.reportReceivedDate)")
    List<Object[]> getRecallCountByYear();
    
    List<Recall> findByReportReceivedDateAfter(LocalDate date);
    
    List<Recall> findByReportReceivedDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT DISTINCT r.component FROM Recall r")
    List<String> findAllComponents();
    
    boolean existsByNhtsaCampaignNumber(String campaignNumber);
}