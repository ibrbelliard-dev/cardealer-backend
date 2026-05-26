package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Make;
import com.cardealer.iotproject.model.entity.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {
    
    Optional<Model> findByMakeAndModelName(Make make, String modelName);
    
    List<Model> findByMake_MakeId(Integer makeId);
    
    List<Model> findByMake_MakeName(String makeName);
    
    List<Model> findByModelNameContainingIgnoreCase(String modelName);
    
    Page<Model> findByMake_MakeId(Integer makeId, Pageable pageable);
    
    @Query("SELECT m FROM Model m WHERE m.make.makeId = :makeId AND m.isActive = true")
    List<Model> findActiveModelsByMakeId(@Param("makeId") Integer makeId);
    
    @Query("SELECT m FROM Model m WHERE m.make.makeName = :makeName AND m.isActive = true")
    List<Model> findActiveModelsByMakeName(@Param("makeName") String makeName);
    
    @Query("SELECT DISTINCT m.make FROM Model m WHERE m.modelName LIKE %:modelName%")
    List<Make> findMakesByModelName(@Param("modelName") String modelName);
    
    @Query("SELECT m FROM Model m WHERE m.modelYearFrom <= :year AND m.modelYearTo >= :year")
    List<Model> findModelsByYear(@Param("year") Integer year);
    
    boolean existsByMake_MakeIdAndModelName(Integer makeId, String modelName);
    
    @Query("SELECT m.modelName, COUNT(v) FROM Model m LEFT JOIN Vehicle v ON v.model = m GROUP BY m.modelName ORDER BY COUNT(v) DESC")
    List<Object[]> getModelPopularity();
}