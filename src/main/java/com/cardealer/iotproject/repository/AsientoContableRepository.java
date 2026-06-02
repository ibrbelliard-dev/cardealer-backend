// src/main/java/com/cardealer/iotproject/repository/AsientoContableRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.AsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoContableRepository extends JpaRepository<AsientoContable, Integer> {
    
    List<AsientoContable> findAllByOrderByFechaDesc();
    
    List<AsientoContable> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    List<AsientoContable> findByEstado(String estado);
    
    Optional<AsientoContable> findByNumeroAsiento(String numeroAsiento);
    
    List<AsientoContable> findByActivoTrueOrderByFechaDesc();
    
    // ========== AGREGAR ESTE MÉTODO ==========
    @Query("SELECT MAX(a.numeroAsiento) FROM AsientoContable a")
    String findLastNumeroAsiento();
    
    @Query("SELECT COALESCE(SUM(a.totalDebe), 0), COALESCE(SUM(a.totalHaber), 0) FROM AsientoContable a WHERE a.fecha BETWEEN :fechaInicio AND :fechaFin AND a.estado = 'APROBADO'")
    List<Object[]> getTotalesByPeriodo(LocalDate fechaInicio, LocalDate fechaFin);
}