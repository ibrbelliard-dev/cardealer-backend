// src/main/java/com/cardealer/iotproject/repository/DetalleAsientoRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.DetalleAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DetalleAsientoRepository extends JpaRepository<DetalleAsiento, Integer> {
    List<DetalleAsiento> findByAsiento_AsientoId(Integer asientoId);
}