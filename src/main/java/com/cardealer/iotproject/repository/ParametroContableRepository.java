// src/main/java/com/cardealer/iotproject/repository/ParametroContableRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.ParametroContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParametroContableRepository extends JpaRepository<ParametroContable, Integer> {
    Optional<ParametroContable> findByClave(String clave);
}