package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.InvoiceSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequence, Long> {
    
    /**
     * Find active sequence by NCF type and sequence type
     */
    Optional<InvoiceSequence> findByNcfTypeAndSequenceTypeAndIsActiveTrue(String ncfType, String sequenceType);
    
    /**
     * Find sequence by NCF type
     */
    Optional<InvoiceSequence> findByNcfType(String ncfType);
    
    /**
     * Check if sequence exists and is active
     */
    boolean existsByNcfTypeAndSequenceTypeAndIsActiveTrue(String ncfType, String sequenceType);
    
    /**
     * Get all active sequences
     */
    @Query("SELECT s FROM InvoiceSequence s WHERE s.isActive = true AND s.validUntil > CURRENT_TIMESTAMP")
    List<InvoiceSequence> findAllActiveSequences();
    
    /**
     * Get sequence usage percentage
     */
    @Query("SELECT (s.currentNumber - s.startRange) * 100.0 / (s.endRange - s.startRange) " +
           "FROM InvoiceSequence s WHERE s.ncfType = :ncfType AND s.sequenceType = :sequenceType")
    Double getSequenceUsagePercentage(@Param("ncfType") String ncfType, 
                                      @Param("sequenceType") String sequenceType);
}