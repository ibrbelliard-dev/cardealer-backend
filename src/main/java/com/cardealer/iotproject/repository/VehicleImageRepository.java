package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.VehicleImage;
import com.cardealer.iotproject.model.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {
    
    List<VehicleImage> findByVehicle_VehicleIdOrderByIsPrimaryDescSortOrderAsc(Long vehicleId);
    
    List<VehicleImage> findByVehicle_VehicleIdAndImageType(Long vehicleId, ImageType imageType);
    
    Optional<VehicleImage> findByVehicle_VehicleIdAndIsPrimaryTrue(Long vehicleId);
    
    long countByVehicle_VehicleId(Long vehicleId);
    
    @Modifying
    @Transactional
    @Query("UPDATE VehicleImage v SET v.isPrimary = false WHERE v.vehicle.vehicleId = :vehicleId")
    void clearPrimaryFlagForVehicle(@Param("vehicleId") Long vehicleId);
    
    @Modifying
    @Transactional
    void deleteByVehicle_VehicleId(Long vehicleId);
    
    List<VehicleImage> findByUploadedBy(String uploadedBy);
    
    List<VehicleImage> findByUploadedAtAfter(LocalDateTime date);
    
    @Query("SELECT COALESCE(MAX(v.sortOrder), -1) FROM VehicleImage v WHERE v.vehicle.vehicleId = :vehicleId")
    Integer getMaxSortOrder(@Param("vehicleId") Long vehicleId);
    
    List<VehicleImage> findByOriginalFilenameContainingIgnoreCase(String filename);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM VehicleImage v WHERE v.uploadedAt < :date")
    void deleteOldImages(@Param("date") LocalDateTime date);
    
    @Query("SELECT v FROM VehicleImage v WHERE v.vehicle.vehicleId = :vehicleId AND v.isPrimary = true")
    Optional<VehicleImage> findPrimaryImageByVehicleId(@Param("vehicleId") Long vehicleId);
    
    // Removed findActiveImagesByVehicleId method
}