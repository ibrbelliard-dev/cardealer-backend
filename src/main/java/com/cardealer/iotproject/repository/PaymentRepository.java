package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByReceiptNumber(String receiptNumber);
    
    List<Payment> findByInvoiceId(Long invoiceId);
    
    Page<Payment> findByInvoiceId(Long invoiceId, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.invoice.id = :invoiceId AND p.status = 'COMPLETED'")
    BigDecimal getTotalPaidByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    Page<Payment> findByPaymentType(String paymentType, Pageable pageable);
    
    Page<Payment> findByPaymentMethod(String paymentMethod, Pageable pageable);
    
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "LOWER(p.receiptNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.paymentMethod) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.paymentType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Payment> searchPayments(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalPaymentsAmount();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED'")
    Long getTotalPaymentsCount();
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentType = :paymentType AND p.status = 'COMPLETED'")
    BigDecimal getTotalByPaymentType(@Param("paymentType") String paymentType);
}