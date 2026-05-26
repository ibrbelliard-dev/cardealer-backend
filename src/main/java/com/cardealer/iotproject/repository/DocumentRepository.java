package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByCategory(String category);
    
    List<Document> findByDocumentType(String documentType);
    
    List<Document> findByIsTemplateTrue();
    
    List<Document> findByRelatedEntityTypeAndRelatedEntityId(String entityType, Long entityId);
    
    @Query("SELECT d FROM Document d WHERE " +
           "(:search IS NULL OR " +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.tags) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Document> searchDocuments(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.category = :category AND d.isTemplate = true")
    List<Document> findTemplatesByCategory(@Param("category") String category);
    
    @Query("SELECT DISTINCT d.category FROM Document d")
    List<String> findAllCategories();
}