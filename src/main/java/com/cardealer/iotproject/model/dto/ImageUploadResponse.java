package com.cardealer.iotproject.model.dto;

import com.cardealer.iotproject.model.enums.ImageType;
import java.time.LocalDateTime;

public class ImageUploadResponse {
    
    private Long imageId;
    private String filename;
    private String originalFilename;
    private String imagePath;
    private String thumbnailPath;
    private ImageType imageType;
    private Boolean isPrimary;
    private Integer fileSize;
    private String fileMimeType;
    private String description;
    private Integer sortOrder;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private String imageUrl;
    private String thumbnailUrl;
    
    public ImageUploadResponse() {}
    
    // Getters and Setters
    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }
    
    public ImageType getImageType() { return imageType; }
    public void setImageType(ImageType imageType) { this.imageType = imageType; }
    
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public Integer getFileSize() { return fileSize; }
    public void setFileSize(Integer fileSize) { this.fileSize = fileSize; }
    
    public String getFileMimeType() { return fileMimeType; }
    public void setFileMimeType(String fileMimeType) { this.fileMimeType = fileMimeType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    // Factory methods
    public static ImageUploadResponse fromEntity(com.cardealer.iotproject.model.entity.VehicleImage image, String baseUrl) {
        ImageUploadResponse response = new ImageUploadResponse();
        response.setImageId(image.getImageId());
        response.setFilename(image.getFilename());
        response.setOriginalFilename(image.getOriginalFilename());
        response.setImagePath(image.getImagePath());
        response.setThumbnailPath(image.getThumbnailPath());
        response.setImageType(image.getImageType());
        response.setIsPrimary(image.getIsPrimary());
        response.setFileSize(image.getFileSize());
        response.setFileMimeType(image.getFileMimeType());
        response.setDescription(image.getDescription());
        response.setSortOrder(image.getSortOrder());
        response.setUploadedBy(image.getUploadedBy());
        response.setUploadedAt(image.getUploadedAt());
        
        if (baseUrl != null) {
            response.setImageUrl(baseUrl + image.getImagePath());
            if (image.getThumbnailPath() != null) {
                response.setThumbnailUrl(baseUrl + image.getThumbnailPath());
            }
        }
        
        return response;
    }
}