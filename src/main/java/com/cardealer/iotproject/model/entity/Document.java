package com.cardealer.iotproject.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_document_type", columnList = "document_type"),
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_uploaded_by", columnList = "uploaded_by")
})
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "document_type", length = 50)
    private String documentType;
    
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;
    
    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;
    
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    
    @Column(name = "version")
    private Integer version = 1;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_template")
    private Boolean isTemplate = false;
    
    @Column(name = "tags", length = 500)
    private String tags;
    
    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;
    
    @Column(name = "access_count")
    private Long accessCount = 0L;
    
    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;
    
    @Column(name = "related_entity_id")
    private Long relatedEntityId;
    
    public Document() {}
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (accessCount == null) accessCount = 0L;
        if (version == null) version = 1;
        if (isActive == null) isActive = true;
        if (isTemplate == null) isTemplate = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getDocumentType() { return documentType; }
    public String getFileName() { return fileName; }
    public String getOriginalFileName() { return originalFileName; }
    public String getFilePath() { return filePath; }
    public Long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public Integer getVersion() { return version; }
    public Boolean getIsActive() { return isActive; }
    public Boolean getIsTemplate() { return isTemplate; }
    public String getTags() { return tags; }
    public String getUploadedBy() { return uploadedBy; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public Long getAccessCount() { return accessCount; }
    public String getRelatedEntityType() { return relatedEntityType; }
    public Long getRelatedEntityId() { return relatedEntityId; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public void setVersion(Integer version) { this.version = version; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setIsTemplate(Boolean isTemplate) { this.isTemplate = isTemplate; }
    public void setTags(String tags) { this.tags = tags; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    public void setAccessCount(Long accessCount) { this.accessCount = accessCount; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    
    // Helper methods
    public String getFormattedFileSize() {
        if (fileSize == null) return "0 B";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.2f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        return String.format("%.2f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
    }
    
    public String getFileExtension() {
        if (originalFileName == null) return "";
        int lastDot = originalFileName.lastIndexOf(".");
        if (lastDot > 0) {
            return originalFileName.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }
    
    public boolean isImage() {
        String ext = getFileExtension();
        return ext.matches("jpg|jpeg|png|gif|webp|bmp");
    }
    
    public boolean isVideo() {
        String ext = getFileExtension();
        return ext.matches("mp4|avi|mov|wmv|flv|mkv|webm");
    }
    
    public boolean isPdf() {
        return "pdf".equals(getFileExtension());
    }
    
    public boolean isWord() {
        return getFileExtension().matches("doc|docx");
    }
    
    public boolean isExcel() {
        return getFileExtension().matches("xls|xlsx");
    }
    
    public boolean isPowerPoint() {
        return getFileExtension().matches("ppt|pptx");
    }
    
    public String getIconClass() {
        if (isPdf()) return "description";
        if (isImage()) return "image";
        if (isVideo()) return "videocam";
        if (isWord()) return "description";
        if (isExcel()) return "table_chart";
        if (isPowerPoint()) return "slideshow";
        return "insert_drive_file";
    }
}