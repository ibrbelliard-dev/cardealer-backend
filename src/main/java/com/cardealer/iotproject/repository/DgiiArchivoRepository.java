// src/main/java/com/cardealer/iotproject/repository/DgiiArchivoRepository.java
package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.DgiiArchivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DgiiArchivoRepository extends JpaRepository<DgiiArchivo, Long> {
    
    Optional<DgiiArchivo> findByTipoArchivoAndPeriodo(String tipoArchivo, String periodo);
    
    List<DgiiArchivo> findByTipoArchivoOrderByPeriodoDesc(String tipoArchivo);
    
    List<DgiiArchivo> findByPeriodoBetween(String inicio, String fin);

    List<DgiiArchivo> findAllByOrderByGeneradoEnDesc();

    
void deleteByTipoArchivo(String tipoArchivo);

void deleteByPeriodo(String periodo);

List<DgiiArchivo> findByPeriodo(String periodo);

}