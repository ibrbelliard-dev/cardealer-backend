package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.InvoiceSequence;
import com.cardealer.iotproject.repository.InvoiceSequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InvoiceSequenceService {
    
    @Autowired
    private InvoiceSequenceRepository sequenceRepository;
    
    private static final String NCF_TYPE_SALE = "31"; // Crédito Fiscal
    private static final String SEQUENCE_TYPE_ELECTRONIC = "ELECTRONIC";
    private static final String PREFIX = "E";
    
    @Transactional
    public Long getNextInvoiceSequence() {
        // Find or create the sequence for NCF type 31 (sale invoices)
        InvoiceSequence sequence = sequenceRepository
            .findByNcfTypeAndSequenceTypeAndIsActiveTrue(NCF_TYPE_SALE, SEQUENCE_TYPE_ELECTRONIC)
            .orElseGet(() -> createDefaultSequence());
        
        // Check if we've reached the end range
        if (sequence.getEndRange() != null && sequence.getCurrentNumber() >= sequence.getEndRange()) {
            throw new RuntimeException("Invoice sequence has reached its maximum limit. Please contact administrator.");
        }
        
        // Generate next number
        Long nextNumber = sequence.getCurrentNumber() + 1;
        sequence.setCurrentNumber(nextNumber);
        
        // Save and return the updated sequence number
        InvoiceSequence saved = sequenceRepository.save(sequence);
        return saved.getCurrentNumber();
    }
    
    @Transactional
    public String generateNextEnNcf() {
        InvoiceSequence sequence = sequenceRepository
            .findByNcfTypeAndSequenceTypeAndIsActiveTrue(NCF_TYPE_SALE, SEQUENCE_TYPE_ELECTRONIC)
            .orElseGet(() -> createDefaultSequence());
        
        // Generate the e-NCF using the entity's method
        String enNcf = sequence.generateNextENcf();
        
        // Save the updated sequence
        sequenceRepository.save(sequence);
        
        return enNcf;
    }
    
    private InvoiceSequence createDefaultSequence() {
        InvoiceSequence sequence = new InvoiceSequence();
        sequence.setNcfType(NCF_TYPE_SALE);
        sequence.setSequenceType(SEQUENCE_TYPE_ELECTRONIC);
        sequence.setPrefix(PREFIX);
        sequence.setCurrentNumber(0L);
        sequence.setStartRange(1L);
        sequence.setEndRange(9999999999L); // Default max
        sequence.setAuthorizationNumber("AUTH-DEFAULT-001");
        sequence.setValidFrom(LocalDateTime.now());
        sequence.setValidUntil(LocalDateTime.now().plusYears(1));
        sequence.setIsActive(true);
        return sequenceRepository.save(sequence);
    }
    
    @Transactional
    public InvoiceSequence initializeSequence(String ncfType, Long startRange, Long endRange, String authorizationNumber) {
        InvoiceSequence sequence = new InvoiceSequence();
        sequence.setNcfType(ncfType);
        sequence.setSequenceType(SEQUENCE_TYPE_ELECTRONIC);
        sequence.setPrefix(PREFIX);
        sequence.setCurrentNumber(startRange - 1); // Will start at startRange when first used
        sequence.setStartRange(startRange);
        sequence.setEndRange(endRange);
        sequence.setAuthorizationNumber(authorizationNumber);
        sequence.setValidFrom(LocalDateTime.now());
        sequence.setValidUntil(LocalDateTime.now().plusYears(1));
        sequence.setIsActive(true);
        
        return sequenceRepository.save(sequence);
    }
    
    public Long getCurrentSequenceNumber() {
        return sequenceRepository
            .findByNcfTypeAndSequenceTypeAndIsActiveTrue(NCF_TYPE_SALE, SEQUENCE_TYPE_ELECTRONIC)
            .map(InvoiceSequence::getCurrentNumber)
            .orElse(0L);
    }
    
    @Transactional
    public boolean validateSequenceRange() {
        return sequenceRepository
            .findByNcfTypeAndSequenceTypeAndIsActiveTrue(NCF_TYPE_SALE, SEQUENCE_TYPE_ELECTRONIC)
            .map(seq -> seq.getEndRange() == null || seq.getCurrentNumber() < seq.getEndRange())
            .orElse(false);
    }
}