// MakeRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Make;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface MakeRepository extends JpaRepository<Make, Integer> {
    Optional<Make> findByMakeName(String makeName);
    List<Make> findByMakeNameContainingIgnoreCase(String name);
    
    @Query("SELECT m FROM Make m WHERE m.makeId IN (SELECT DISTINCT v.make.makeId FROM Vehicle v WHERE v.status = 'AVAILABLE')")
    List<Make> findMakesWithAvailableVehicles();
}