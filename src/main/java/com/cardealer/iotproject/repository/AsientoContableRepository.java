// src/main/java/com/cardealer/iotproject/repository/AsientoContableRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.AsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoContableRepository extends JpaRepository<AsientoContable, Integer> {
    List<AsientoContable> findByActivoTrueOrderByFechaDesc();
    Optional<AsientoContable> findByNumeroAsiento(String numeroAsiento);
    
    @Query("SELECT MAX(a.numeroAsiento) FROM AsientoContable a")
    String findLastNumeroAsiento();
}