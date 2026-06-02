// src/main/java/com/cardealer/iotproject/repository/RolRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
    List<Rol> findByActivoTrue();
}