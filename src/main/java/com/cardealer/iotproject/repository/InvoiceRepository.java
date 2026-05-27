package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Invoice;
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
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    /**
     * Find invoice by electronic NCF number
     */
    Optional<Invoice> findByEnNcf(String enNcf);
    
    /**
     * Find invoices by customer RNC (business tax ID)
     */
    List<Invoice> findByCustomerRnc(String customerRnc);
    
    /**
     * Find invoices by customer cedula (individual ID)
     */
    List<Invoice> findByCustomerCedula(String customerCedula);
    
    /**
     * Find invoices by vehicle ID
     */
    List<Invoice> findByVehicleVehicleId(Long vehicleId);
    
    /**
     * Find invoices by client ID
     */
    List<Invoice> findByClientId(Long clientId);
    
    /**
     * Find invoices by sales representative ID
     */
    List<Invoice> findBySalesRepId(Long salesRepId);
    
    /**
     * Find invoices by status
     */
    List<Invoice> findByStatus(String status);
    
    /**
     * Find invoices by date range
     */
    @Query("SELECT i FROM Invoice i WHERE i.invoiceDateTime BETWEEN :startDate AND :endDate ORDER BY i.invoiceDateTime DESC")
    List<Invoice> findByInvoiceDateBetween(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Search invoices by multiple criteria
     */
    @Query("SELECT i FROM Invoice i WHERE " +
           "(:search IS NULL OR " +
           "LOWER(i.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.enNcf) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.customerRnc) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.customerCedula) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Invoice> searchInvoices(@Param("search") String search, Pageable pageable);
    
    /**
     * Get monthly invoice statistics (current month)
     */
    @Query("SELECT COUNT(i), COALESCE(SUM(i.total), 0), COALESCE(SUM(i.itbisAmount), 0) " +
           "FROM Invoice i WHERE i.status = 'PAID' " +
           "AND MONTH(i.invoiceDateTime) = MONTH(CURRENT_DATE) " +
           "AND YEAR(i.invoiceDateTime) = YEAR(CURRENT_DATE)")
    List<Object[]> getMonthlyInvoiceStats();
    
    /**
     * Count invoices by NCF type
     */
    @Query("SELECT i.ncfType, COUNT(i) FROM Invoice i GROUP BY i.ncfType")
    List<Object[]> countByNcfType();
    
    /**
     * Get total revenue from all paid invoices
     */
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.status = 'PAID'")
    BigDecimal getTotalRevenue();
    
    /**
     * Get total ITBIS collected from all paid invoices
     */
    @Query("SELECT COALESCE(SUM(i.itbisAmount), 0) FROM Invoice i WHERE i.status = 'PAID'")
    BigDecimal getTotalItbisCollected();
    
    /**
     * Get invoices by payment method
     */
    List<Invoice> findByPaymentMethod(String paymentMethod);
    
    /**
     * Get invoices by invoice type
     */
    List<Invoice> findByInvoiceType(String invoiceType);
    
    /**
     * Get invoices for a specific date
     */
    @Query("SELECT i FROM Invoice i WHERE DATE(i.invoiceDateTime) = :date")
    List<Invoice> findByInvoiceDate(@Param("date") LocalDateTime date);
    
    /**
     * Get pending invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.status = 'PENDING' ORDER BY i.invoiceDateTime ASC")
    List<Invoice> findPendingInvoices();
    
    /**
     * Get paid invoices for a specific period
     */
    @Query("SELECT i FROM Invoice i WHERE i.status = 'PAID' " +
           "AND i.invoiceDateTime BETWEEN :startDate AND :endDate")
    List<Invoice> findPaidInvoicesBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get monthly revenue for the last 12 months
     */
    @Query("SELECT YEAR(i.invoiceDateTime) as year, MONTH(i.invoiceDateTime) as month, " +
           "COALESCE(SUM(i.total), 0) as total " +
           "FROM Invoice i WHERE i.status = 'PAID' " +
           "AND i.invoiceDateTime >= :startDate " +
           "GROUP BY YEAR(i.invoiceDateTime), MONTH(i.invoiceDateTime) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyRevenueReport(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Get invoices by NCF type and date range
     */
    @Query("SELECT i FROM Invoice i WHERE i.ncfType = :ncfType " +
           "AND i.invoiceDateTime BETWEEN :startDate AND :endDate")
    List<Invoice> findByNcfTypeAndDateRange(@Param("ncfType") String ncfType,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get all invoices for a specific customer (by RNC or Cedula)
     */
    @Query("SELECT i FROM Invoice i WHERE i.customerRnc = :customerId OR i.customerCedula = :customerId")
    List<Invoice> findByCustomerId(@Param("customerId") String customerId);
    
    /**
     * Count invoices by status
     */
    @Query("SELECT i.status, COUNT(i) FROM Invoice i GROUP BY i.status")
    List<Object[]> countByStatus();
    
    /**
     * Get total sales by sales representative
     */
    @Query("SELECT i.salesRep.id, i.salesRep.firstName, i.salesRep.lastName, " +
           "COALESCE(SUM(i.total), 0) " +
           "FROM Invoice i WHERE i.status = 'PAID' " +
           "GROUP BY i.salesRep.id, i.salesRep.firstName, i.salesRep.lastName " +
           "ORDER BY SUM(i.total) DESC")
    List<Object[]> getSalesBySalesRep();
    
    /**
     * Get today's invoices
     */
    @Query("SELECT i FROM Invoice i WHERE DATE(i.invoiceDateTime) = CURRENT_DATE")
    List<Invoice> findTodaysInvoices();
    
    /**
     * Get invoices with high value (above threshold)
     */
    @Query("SELECT i FROM Invoice i WHERE i.total > :threshold ORDER BY i.total DESC")
    List<Invoice> findHighValueInvoices(@Param("threshold") BigDecimal threshold);
    
    /**
     * Check if invoice exists by NCF
     */
    boolean existsByEnNcf(String enNcf);
    
    /**
     * Get latest invoices (limit)
     */
    @Query("SELECT i FROM Invoice i ORDER BY i.invoiceDateTime DESC")
    List<Invoice> findLatestInvoices(Pageable pageable);
    
    /**
     * Get invoices by vehicle VIN
     */
    @Query("SELECT i FROM Invoice i WHERE i.vehicle.vin LIKE CONCAT('%', :vin, '%')")
    List<Invoice> findByVehicleVin(@Param("vin") String vin);
    
    
    /**
     * Get cancelled invoices with reason
     */
    @Query("SELECT i FROM Invoice i WHERE i.status = 'CANCELLED' AND i.cancelledAt >= :date")
    List<Invoice> findCancelledInvoicesSince(@Param("date") LocalDateTime date);


    List<Invoice> findByStatusIn(List<String> statuses);

@Query("SELECT i FROM Invoice i WHERE i.status IN :statuses AND i.invoiceDateTime BETWEEN :start AND :end")
List<Invoice> findByStatusInAndInvoiceDateTimeBetween(@Param("statuses") List<String> statuses,
                                                        @Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);
// src/main/java/com/cardealer/iotproject/repository/InvoiceRepository.java




@Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.status IN :statuses")
BigDecimal getTotalRevenueByStatus(@Param("statuses") List<String> statuses);



}