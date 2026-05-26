package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.config.AppConfig;
import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.dto.ImageUploadResponse;
import com.cardealer.iotproject.model.entity.VehicleImage;
import com.cardealer.iotproject.model.enums.ImageType;
import com.cardealer.iotproject.service.CarImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/car-images")
@Tag(name = "Car Images", description = "Endpoints for car/vehicle image management")
public class CarImageController {
    
    private final CarImageService carImageService;
    private final AppConfig appConfig;
    
    public CarImageController(CarImageService carImageService, AppConfig appConfig) {
        this.carImageService = carImageService;
        this.appConfig = appConfig;
    }
    
    /**
     * Upload images for a vehicle
     */
    @PostMapping(value = "/vehicles/{vehicleId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload images for a vehicle")
    public ResponseEntity<ApiResponse> uploadImages(
            @PathVariable Long vehicleId,
            @RequestParam("images") MultipartFile[] files,
            @RequestParam(required = false) ImageType imageType,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "system") String uploadedBy) throws IOException {
        
        List<VehicleImage> images = carImageService.uploadImages(vehicleId, files, imageType, description, uploadedBy);
        
        String baseUrl = appConfig.getApiBaseUrl();
        
        List<ImageUploadResponse> response = images.stream()
            .map(img -> ImageUploadResponse.fromEntity(img, baseUrl))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(
            String.format("Successfully uploaded %d image(s)", response.size()), 
            response
        ));
    }
    
    /**
     * Get all images for a vehicle
     */
    @GetMapping("/vehicles/{vehicleId}")
    @Operation(summary = "Get all images for a vehicle")
    public ResponseEntity<ApiResponse> getVehicleImages(@PathVariable Long vehicleId) {
        List<VehicleImage> images = carImageService.getImagesByVehicleId(vehicleId);
        
        String baseUrl = appConfig.getApiBaseUrl();
        
        List<Map<String, Object>> response = images.stream()
            .map(img -> {
                Map<String, Object> imageMap = new HashMap<>();
                imageMap.put("imageId", img.getImageId());
                imageMap.put("filename", img.getFilename());
                imageMap.put("originalFilename", img.getOriginalFilename());
                imageMap.put("imageUrl", baseUrl + "/api/car-images/show/" + vehicleId + "/" + img.getFilename());
                imageMap.put("thumbnailUrl", img.getThumbnailPath() != null 
                    ? baseUrl + "/api/car-images/show/" + vehicleId + "/" + new File(img.getThumbnailPath()).getName() 
                    : null);
                imageMap.put("imageType", img.getImageType());
                imageMap.put("isPrimary", img.getIsPrimary());
                imageMap.put("description", img.getDescription());
                imageMap.put("fileSize", img.getFileSize());
                imageMap.put("uploadedAt", img.getUploadedAt());
                return imageMap;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Images retrieved successfully", response));
    }
    
    /**
     * Get primary image for a vehicle
     */
    @GetMapping("/vehicles/{vehicleId}/primary")
    @Operation(summary = "Get primary image for a vehicle")
    public ResponseEntity<ApiResponse> getPrimaryImage(@PathVariable Long vehicleId) {
        VehicleImage primaryImage = carImageService.getPrimaryImage(vehicleId);
        
        if (primaryImage == null) {
            return ResponseEntity.ok(ApiResponse.success("No primary image found", null));
        }
        
        String baseUrl = appConfig.getApiBaseUrl();
        ImageUploadResponse response = ImageUploadResponse.fromEntity(primaryImage, baseUrl);
        
        return ResponseEntity.ok(ApiResponse.success("Primary image retrieved", response));
    }
    
    /**
     * Set an image as primary
     */
    @PutMapping("/{imageId}/primary")
    @Operation(summary = "Set an image as primary")
    public ResponseEntity<ApiResponse> setPrimaryImage(@PathVariable Long imageId) {
        carImageService.setPrimaryImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Primary image updated successfully", null));
    }
    
    /**
     * Update image metadata
     */
    @PutMapping("/{imageId}")
    @Operation(summary = "Update image metadata")
    public ResponseEntity<ApiResponse> updateImageMetadata(
            @PathVariable Long imageId,
            @RequestParam(required = false) ImageType imageType,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer sortOrder) {
        
        carImageService.updateImageMetadata(imageId, imageType, description, sortOrder);
        return ResponseEntity.ok(ApiResponse.success("Image metadata updated successfully", null));
    }
    
    /**
     * Delete an image
     */
    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete an image")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) throws IOException {
        carImageService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
    }
    
    /**
     * Delete all images for a vehicle
     */
    @DeleteMapping("/vehicles/{vehicleId}")
    @Operation(summary = "Delete all images for a vehicle")
    public ResponseEntity<ApiResponse> deleteAllVehicleImages(@PathVariable Long vehicleId) throws IOException {
        int count = carImageService.deleteAllVehicleImages(vehicleId);
        return ResponseEntity.ok(ApiResponse.success(
            String.format("Deleted %d image(s) for vehicle ID: %d", count, vehicleId), 
            null
        ));
    }
    
    /**
     * Reorder images
     */
    @PutMapping("/vehicles/{vehicleId}/reorder")
    @Operation(summary = "Reorder vehicle images")
    public ResponseEntity<ApiResponse> reorderImages(
            @PathVariable Long vehicleId,
            @RequestBody List<Long> imageIdsInOrder) {
        
        carImageService.reorderImages(vehicleId, imageIdsInOrder);
        return ResponseEntity.ok(ApiResponse.success("Images reordered successfully", null));
    }

    /**
     * Directly serve image file
     */
    @GetMapping("/show/{vehicleId}/{filename}")
    @Operation(summary = "Directly serve image file")
    public ResponseEntity<byte[]> serveImage(@PathVariable Long vehicleId, @PathVariable String filename) {
        try {
            Path imagePath = Paths.get("./uploads/vehicles/" + vehicleId + "/" + filename);
            
            if (!Files.exists(imagePath)) {
                imagePath = Paths.get("uploads/vehicles/" + vehicleId + "/" + filename);
            }
            
            if (!Files.exists(imagePath)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageBytes = Files.readAllBytes(imagePath);
            String contentType = Files.probeContentType(imagePath);
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg"))
                .body(imageBytes);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}