package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    
    /**
     * Find all items for a specific invoice
     */
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
    
    /**
     * Delete all items for a specific invoice
     */
    @Modifying
    @Transactional
    void deleteByInvoiceId(Long invoiceId);
    
    /**
     * Get total amount for an invoice
     */
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM InvoiceItem i WHERE i.invoice.id = :invoiceId")
    BigDecimal getInvoiceItemsTotal(@Param("invoiceId") Long invoiceId);
    
    /**
     * Get item count for an invoice
     */
    @Query("SELECT COUNT(i) FROM InvoiceItem i WHERE i.invoice.id = :invoiceId")
    long countByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    /**
     * Find items by item type
     */
    List<InvoiceItem> findByItemType(String itemType);
    
    /**
     * Find items by vehicle reference (stored in description)
     */
    @Query("SELECT i FROM InvoiceItem i WHERE i.description LIKE CONCAT('%', :vehicleInfo, '%')")
    List<InvoiceItem> findByVehicleInfo(@Param("vehicleInfo") String vehicleInfo);
}