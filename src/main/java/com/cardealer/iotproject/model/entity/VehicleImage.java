package com.cardealer.iotproject.model.entity;

import com.cardealer.iotproject.model.enums.ImageType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_images")
public class VehicleImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;
    
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;
    
    @Column(name = "file_size", nullable = false)
    private Integer fileSize;
    
    @Column(name = "file_mime_type", length = 100)
    private String fileMimeType;
    
    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath;
    
    @Column(name = "thumbnail_path", length = 500)
    private String thumbnailPath;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type")
    private ImageType imageType = ImageType.OTHER;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "is_active")  // Added missing field
    private Boolean isActive = true;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public VehicleImage() {}
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
        if (isPrimary == null) isPrimary = false;
        if (isActive == null) isActive = true;
        if (sortOrder == null) sortOrder = 0;
        if (imageType == null) imageType = ImageType.OTHER;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    
    public Integer getFileSize() { return fileSize; }
    public void setFileSize(Integer fileSize) { this.fileSize = fileSize; }
    
    public String getFileMimeType() { return fileMimeType; }
    public void setFileMimeType(String fileMimeType) { this.fileMimeType = fileMimeType; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }
    
    public ImageType getImageType() { return imageType; }
    public void setImageType(ImageType imageType) { this.imageType = imageType; }
    
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}