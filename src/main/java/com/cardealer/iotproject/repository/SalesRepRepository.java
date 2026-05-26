package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.SalesRep;
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
public interface SalesRepRepository extends JpaRepository<SalesRep, Long> {
    
    Optional<SalesRep> findByCedula(String cedula);
    
    Optional<SalesRep> findByEmail(String email);
    
    List<SalesRep> findByStatus(Integer status);
    
    @Query("SELECT s FROM SalesRep s WHERE " +
           "(:search IS NULL OR " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.cedula) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SalesRep> searchSalesReps(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(s.totalSales), 0) FROM SalesRep s")
    BigDecimal getTotalSalesAllReps();
    
    @Query("SELECT s FROM SalesRep s ORDER BY s.totalSales DESC")
    List<SalesRep> findTopPerformingSalesReps(Pageable pageable);
    
    boolean existsByCedula(String cedula);
    
    boolean existsByEmail(String email);
}