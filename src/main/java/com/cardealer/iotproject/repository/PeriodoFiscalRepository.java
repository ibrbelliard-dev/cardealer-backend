// src/main/java/com/cardealer/iotproject/repository/PeriodoFiscalRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.PeriodoFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoFiscalRepository extends JpaRepository<PeriodoFiscal, Integer> {

    List<PeriodoFiscal> findByActivoTrueOrderByAnioDesc();

    Optional<PeriodoFiscal> findByEstadoAndActivoTrue(String estado);

    @Query("SELECT p FROM PeriodoFiscal p WHERE p.estado = 'ABIERTO' AND p.activo = true")
    Optional<PeriodoFiscal> findPeriodoAbierto();

    boolean existsByAnioAndActivoTrue(Integer anio);
    
    Optional<PeriodoFiscal> findByPeriodoIdAndActivoTrue(Integer periodoId);
}