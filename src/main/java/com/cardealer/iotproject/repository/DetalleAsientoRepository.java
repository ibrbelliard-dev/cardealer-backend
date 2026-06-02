// src/main/java/com/cardealer/iotproject/repository/DetalleAsientoRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.DetalleAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleAsientoRepository extends JpaRepository<DetalleAsiento, Integer> {
    
    /**
     * Find all detalles for a specific asiento
     */
    List<DetalleAsiento> findByAsiento_AsientoId(Integer asientoId);
    
    /**
     * Find all detalles for a specific asiento with cuenta filter
     */
    List<DetalleAsiento> findByAsiento_AsientoIdAndCuentaCodigo(Integer asientoId, String cuentaCodigo);
    
    /**
     * Get total debe by cuenta for a period
     */
    @Query("SELECT COALESCE(SUM(d.debe), 0) FROM DetalleAsiento d WHERE d.cuentaCodigo = :cuentaCodigo AND d.asiento.fecha BETWEEN :fechaInicio AND :fechaFin")
    Double getTotalDebeByCuentaAndPeriodo(@Param("cuentaCodigo") String cuentaCodigo, 
                                           @Param("fechaInicio") java.time.LocalDate fechaInicio, 
                                           @Param("fechaFin") java.time.LocalDate fechaFin);
    
    /**
     * Get total haber by cuenta for a period
     */
    @Query("SELECT COALESCE(SUM(d.haber), 0) FROM DetalleAsiento d WHERE d.cuentaCodigo = :cuentaCodigo AND d.asiento.fecha BETWEEN :fechaInicio AND :fechaFin")
    Double getTotalHaberByCuentaAndPeriodo(@Param("cuentaCodigo") String cuentaCodigo, 
                                            @Param("fechaInicio") java.time.LocalDate fechaInicio, 
                                            @Param("fechaFin") java.time.LocalDate fechaFin);
    
    /**
     * Find all detalles by cuenta codigo
     */
    List<DetalleAsiento> findByCuentaCodigo(String cuentaCodigo);
    
    /**
     * Delete all detalles for a specific asiento
     */
    void deleteByAsiento_AsientoId(Integer asientoId);
}