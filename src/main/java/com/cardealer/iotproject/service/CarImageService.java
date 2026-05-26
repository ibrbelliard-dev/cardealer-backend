package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.Vehicle;
import com.cardealer.iotproject.model.entity.VehicleImage;
import com.cardealer.iotproject.model.enums.ImageType;
import com.cardealer.iotproject.repository.VehicleImageRepository;
import com.cardealer.iotproject.repository.VehicleRepository;
import com.cardealer.iotproject.util.ImageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class CarImageService {
    
    private static final Logger log = Logger.getLogger(CarImageService.class.getName());
    
    @Autowired
    private VehicleImageRepository vehicleImageRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private ImageProcessor imageProcessor;
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    /**
     * Upload images for a vehicle
     */
    @Transactional
    public List<VehicleImage> uploadImages(Long vehicleId, MultipartFile[] files, 
                                           ImageType imageType, String description, 
                                           String uploadedBy) throws IOException {
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        
        List<VehicleImage> uploadedImages = new ArrayList<>();
        Path vehicleDir = Paths.get(uploadDir, "vehicles", vehicleId.toString());
        
        if (!Files.exists(vehicleDir)) {
            Files.createDirectories(vehicleDir);
        }
        
        long currentImageCount = vehicleImageRepository.countByVehicle_VehicleId(vehicleId);
        
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            
            // Validate file
            if (!imageProcessor.isValidImage(file)) {
                log.warning("Invalid image file: " + file.getOriginalFilename());
                continue;
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            
            // Save original image
            Path filePath = vehicleDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // Generate thumbnail
            String thumbnailFilename = "thumb_" + filename;
            Path thumbnailPath = vehicleDir.resolve(thumbnailFilename);
            imageProcessor.createThumbnail(filePath, thumbnailPath, 300, 200);
            
            // Create image entity
            VehicleImage image = new VehicleImage();
            image.setVehicle(vehicle);
            image.setFilename(filename);
            image.setOriginalFilename(originalFilename);
            image.setFileSize((int) file.getSize());
            image.setFileMimeType(file.getContentType());
            image.setImagePath("/uploads/vehicles/" + vehicleId + "/" + filename);
            image.setThumbnailPath("/uploads/vehicles/" + vehicleId + "/" + thumbnailFilename);
            image.setImageType(imageType != null ? imageType : ImageType.OTHER);
            image.setDescription(description);
            image.setUploadedBy(uploadedBy);
            image.setSortOrder((int) (currentImageCount + i));
            
            // If this is the first image, make it primary
            if (currentImageCount == 0 && i == 0) {
                image.setIsPrimary(true);
            }
            
            uploadedImages.add(vehicleImageRepository.save(image));
        }
        
        log.info("Uploaded " + uploadedImages.size() + " images for vehicle ID: " + vehicleId);
        return uploadedImages;
    }
    
    /**
     * Get all images for a vehicle
     */
    @Transactional(readOnly = true)
    public List<VehicleImage> getImagesByVehicleId(Long vehicleId) {
        return vehicleImageRepository.findByVehicle_VehicleIdOrderByIsPrimaryDescSortOrderAsc(vehicleId);
    }
    
    /**
     * Get primary image for a vehicle
     */
    @Transactional(readOnly = true)
    public VehicleImage getPrimaryImage(Long vehicleId) {
        return vehicleImageRepository.findByVehicle_VehicleIdAndIsPrimaryTrue(vehicleId).orElse(null);
    }
    
    /**
     * Set an image as primary
     */
    @Transactional
    public void setPrimaryImage(Long imageId) {
        VehicleImage image = vehicleImageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));
        
        // Clear primary flag for all images of this vehicle
        vehicleImageRepository.clearPrimaryFlagForVehicle(image.getVehicle().getVehicleId());
        
        // Set new primary
        image.setIsPrimary(true);
        vehicleImageRepository.save(image);
        
        log.info("Set image ID " + imageId + " as primary");
    }
    
    /**
     * Update image metadata
     */
    @Transactional
    public void updateImageMetadata(Long imageId, ImageType imageType, String description, Integer sortOrder) {
        VehicleImage image = vehicleImageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));
        
        if (imageType != null) {
            image.setImageType(imageType);
        }
        if (description != null) {
            image.setDescription(description);
        }
        if (sortOrder != null) {
            image.setSortOrder(sortOrder);
        }
        
        vehicleImageRepository.save(image);
        log.info("Updated metadata for image ID: " + imageId);
    }
    
    /**
     * Delete an image
     */
    @Transactional
    public void deleteImage(Long imageId) throws IOException {
        VehicleImage image = vehicleImageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));
        
        // Delete physical files
        Path imagePath = Paths.get(uploadDir, image.getImagePath().replace("/uploads/", ""));
        Path thumbnailPath = Paths.get(uploadDir, image.getThumbnailPath().replace("/uploads/", ""));
        
        Files.deleteIfExists(imagePath);
        Files.deleteIfExists(thumbnailPath);
        
        vehicleImageRepository.delete(image);
        log.info("Deleted image ID " + imageId);
    }
    
    /**
     * Delete all images for a vehicle
     */
    @Transactional
    public int deleteAllVehicleImages(Long vehicleId) throws IOException {
        List<VehicleImage> images = vehicleImageRepository.findByVehicle_VehicleIdOrderByIsPrimaryDescSortOrderAsc(vehicleId);
        int count = images.size();
        
        for (VehicleImage image : images) {
            // Delete physical files
            Path imagePath = Paths.get(uploadDir, image.getImagePath().replace("/uploads/", ""));
            Path thumbnailPath = Paths.get(uploadDir, image.getThumbnailPath().replace("/uploads/", ""));
            
            Files.deleteIfExists(imagePath);
            Files.deleteIfExists(thumbnailPath);
        }
        
        vehicleImageRepository.deleteByVehicle_VehicleId(vehicleId);
        log.info("Deleted " + count + " images for vehicle ID: " + vehicleId);
        
        return count;
    }
    
    /**
     * Reorder images for a vehicle
     */
    @Transactional
    public void reorderImages(Long vehicleId, List<Long> imageIdsInOrder) {
        for (int i = 0; i < imageIdsInOrder.size(); i++) {
            Long imageId = imageIdsInOrder.get(i);
            VehicleImage image = vehicleImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));
            
            if (!image.getVehicle().getVehicleId().equals(vehicleId)) {
                throw new RuntimeException("Image does not belong to vehicle: " + vehicleId);
            }
            
            image.setSortOrder(i);
            vehicleImageRepository.save(image);
        }
        log.info("Reordered images for vehicle ID: " + vehicleId);
    }
    
    /**
     * Get total image count for a vehicle
     */
    @Transactional(readOnly = true)
    public long getImageCount(Long vehicleId) {
        return vehicleImageRepository.countByVehicle_VehicleId(vehicleId);
    }
    
    /**
     * Get images by type for a vehicle
     */
    @Transactional(readOnly = true)
    public List<VehicleImage> getImagesByType(Long vehicleId, ImageType imageType) {
        return vehicleImageRepository.findByVehicle_VehicleIdAndImageType(vehicleId, imageType);
    }
    
    /**
     * Check if vehicle has images
     */
    @Transactional(readOnly = true)
    public boolean hasImages(Long vehicleId) {
        return vehicleImageRepository.countByVehicle_VehicleId(vehicleId) > 0;
    }
    
    /**
     * Get the primary image URL for a vehicle
     */
    @Transactional(readOnly = true)
    public String getPrimaryImageUrl(Long vehicleId, String baseUrl) {
        VehicleImage primaryImage = getPrimaryImage(vehicleId);
        if (primaryImage != null) {
            return baseUrl + primaryImage.getImagePath();
        }
        return null;
    }
    
    /**
     * Get all thumbnail URLs for a vehicle
     */
    @Transactional(readOnly = true)
    public List<String> getAllThumbnailUrls(Long vehicleId, String baseUrl) {
        List<VehicleImage> images = getImagesByVehicleId(vehicleId);
        List<String> thumbnailUrls = new ArrayList<>();
        
        for (VehicleImage image : images) {
            if (image.getThumbnailPath() != null) {
                thumbnailUrls.add(baseUrl + image.getThumbnailPath());
            }
        }
        
        return thumbnailUrls;
    }
}