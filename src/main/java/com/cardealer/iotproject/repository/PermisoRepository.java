// src/main/java/com/cardealer/iotproject/repository/PermisoRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    List<Permiso> findByModulo(String modulo);
}