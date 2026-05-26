// src/main/java/com/cardealer/iotproject/repository/CommissionRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Commission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.math.BigDecimal;


@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    
    // ========== MÉTODOS EXISTENTES (ya estaban en tu sistema) ==========
    
    Page<Commission> findBySalesRepId(Long salesRepId, Pageable pageable);
    
    Page<Commission> findByStatus(String status, Pageable pageable);
    
    Page<Commission> findBySalesRepIdAndStatus(Long salesRepId, String status, Pageable pageable);
    
    @Query("SELECT c FROM Commission c WHERE c.createdAt BETWEEN :start AND :end")
    Page<Commission> findByCreatedAtBetween(@Param("start") LocalDateTime start, 
                                             @Param("end") LocalDateTime end, 
                                             Pageable pageable);
    
    Optional<Commission> findByVehicleId(Long vehicleId);
    
    boolean existsByVehicleId(Long vehicleId);
    
    List<Commission> findByStatus(String status);
    
    List<Commission> findBySalesRepId(Long salesRepId);
    
    // ========== MÉTODOS ADICIONALES (agregados para reportes) ==========
    
    @Query("SELECT COALESCE(SUM(c.commissionAmount), 0) FROM Commission c WHERE c.status = 'PENDING'")
    BigDecimal getTotalPendingAll();
    
    @Query("SELECT COALESCE(SUM(c.commissionAmount), 0) FROM Commission c WHERE c.status = 'PAID'")
    BigDecimal getTotalPaidAll();
    
    @Query("SELECT COUNT(c) FROM Commission c WHERE c.status = 'PENDING'")
    Long countPendingAll();
    
    @Query("SELECT COUNT(c) FROM Commission c WHERE c.status = 'PAID'")
    Long countPaidAll();
    
    @Query("SELECT COALESCE(SUM(c.commissionAmount), 0) FROM Commission c WHERE c.createdAt BETWEEN :start AND :end")
    BigDecimal getTotalBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT c FROM Commission c WHERE c.status = :status AND c.paymentDate BETWEEN :start AND :end")
    List<Commission> findByStatusAndPaymentDateBetween(@Param("status") String status,
                                                        @Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);
}