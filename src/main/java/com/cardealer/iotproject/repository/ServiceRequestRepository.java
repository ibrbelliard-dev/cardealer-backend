package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.ServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    
    @Query("SELECT s FROM ServiceRequest s LEFT JOIN FETCH s.vehicle")
    List<ServiceRequest> findAllWithVehicle();
    
    @Query("SELECT s FROM ServiceRequest s LEFT JOIN FETCH s.vehicle WHERE s.id = :id")
    ServiceRequest findByIdWithVehicle(@Param("id") Long id);
    
    @Query("SELECT s FROM ServiceRequest s LEFT JOIN FETCH s.vehicle WHERE s.vehicle.vehicleId = :vehicleId")
    List<ServiceRequest> findByVehicleVehicleIdWithVehicle(@Param("vehicleId") Long vehicleId);
    
    List<ServiceRequest> findByVehicleVehicleId(Long vehicleId);
    
    List<ServiceRequest> findByStatus(String status);
    
    
    List<ServiceRequest> findByMechanic(String mechanic);
    
    @Query("SELECT s FROM ServiceRequest s LEFT JOIN FETCH s.vehicle WHERE s.serviceDate BETWEEN :startDate AND :endDate")
    List<ServiceRequest> findByServiceDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM ServiceRequest s LEFT JOIN FETCH s.vehicle WHERE s.status = :status ORDER BY s.createdAt DESC")
    Page<ServiceRequest> findByStatusOrderByCreatedAtDesc(@Param("status") String status, Pageable pageable);
    
    @Query("SELECT s FROM ServiceRequest s LEFT JOIN FETCH s.vehicle WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:mechanic IS NULL OR LOWER(s.mechanic) LIKE LOWER(CONCAT('%', :mechanic, '%')))")
    Page<ServiceRequest> searchServiceRequests(@Param("status") String status,
                                                @Param("mechanic") String mechanic,
                                                Pageable pageable);
    
    @Query("SELECT COUNT(s) FROM ServiceRequest s WHERE s.status = :status")
    long countByStatus(@Param("status") String status);
    
    @Query("SELECT s.status, COUNT(s) FROM ServiceRequest s GROUP BY s.status")
    List<Object[]> countByStatusGroup();


    
}