package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Optional<Client> findByCedula(String cedula);
    
    Optional<Client> findByRnc(String rnc);
    
    List<Client> findByFirstnameContainingIgnoreCase(String firstname);
    
    List<Client> findByLastnameContainingIgnoreCase(String lastname);
    
    List<Client> findByEmpresaContainingIgnoreCase(String empresa);
    
    List<Client> findByTipo(Integer tipo);
    
    List<Client> findByStatus(Integer status);
    
    @Query("SELECT c FROM Client c WHERE " +
           "(:search IS NULL OR " +
           "LOWER(c.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.cedula) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.rnc) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.empresa) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Client> searchClients(@Param("search") String search, Pageable pageable);
    
    boolean existsByCedula(String cedula);
    
    boolean existsByRnc(String rnc);
    
    @Query("SELECT c.tipo, COUNT(c) FROM Client c GROUP BY c.tipo")
    List<Object[]> countByTipo();
    
    @Query("SELECT c.status, COUNT(c) FROM Client c GROUP BY c.status")
    List<Object[]> countByStatus();
}