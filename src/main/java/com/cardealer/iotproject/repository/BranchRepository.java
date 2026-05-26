package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    
    /**
     * Find all active branches ordered by name
     */
    List<Branch> findByIsActiveTrueOrderByBranchNameAsc();
    
    /**
     * Find all branches ordered by name
     */
    List<Branch> findAllByOrderByBranchNameAsc();
    
    /**
     * Check if a branch exists by name (case insensitive)
     */
    boolean existsByBranchNameIgnoreCase(String branchName);
    
    /**
     * Find branch by name (case insensitive)
     */
    Optional<Branch> findByBranchNameIgnoreCase(String branchName);
    
    /**
     * Find branches by city
     */
    List<Branch> findByCityContainingIgnoreCase(String city);
    
    /**
     * Find branches by province
     */
    List<Branch> findByProvinciaContainingIgnoreCase(String provincia);
    
    /**
     * Count active branches
     */
    long countByIsActiveTrue();
}