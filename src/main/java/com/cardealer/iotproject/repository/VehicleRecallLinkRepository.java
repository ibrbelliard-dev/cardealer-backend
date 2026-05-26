package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.VehicleRecallLink;
import com.cardealer.iotproject.model.entity.VehicleRecallLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VehicleRecallLinkRepository extends JpaRepository<VehicleRecallLink, VehicleRecallLinkId> {
    
    /**
     * Find all recall links for a specific vehicle
     */
    @Query("SELECT vrl FROM VehicleRecallLink vrl WHERE vrl.id.vehicleId = :vehicleId")
    List<VehicleRecallLink> findByVehicleId(@Param("vehicleId") Long vehicleId);
    
    /**
     * Find all recall links for a specific recall
     */
    @Query("SELECT vrl FROM VehicleRecallLink vrl WHERE vrl.id.recallId = :recallId")
    List<VehicleRecallLink> findByRecallId(@Param("recallId") Long recallId);
    
    /**
     * Count recall links for a specific vehicle
     */
    @Query("SELECT COUNT(vrl) FROM VehicleRecallLink vrl WHERE vrl.id.vehicleId = :vehicleId")
    long countByVehicleId(@Param("vehicleId") Long vehicleId);
    
    /**
     * Count recall links for a specific recall
     */
    @Query("SELECT COUNT(vrl) FROM VehicleRecallLink vrl WHERE vrl.id.recallId = :recallId")
    long countByRecallId(@Param("recallId") Long recallId);
    
    /**
     * Check if a specific vehicle-recall link exists
     */
    @Query("SELECT CASE WHEN COUNT(vrl) > 0 THEN true ELSE false END FROM VehicleRecallLink vrl WHERE vrl.id.vehicleId = :vehicleId AND vrl.id.recallId = :recallId")
    boolean existsByVehicleIdAndRecallId(@Param("vehicleId") Long vehicleId, @Param("recallId") Long recallId);
    
    /**
     * Delete all recall links for a specific vehicle
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM VehicleRecallLink vrl WHERE vrl.id.vehicleId = :vehicleId")
    void deleteByVehicleId(@Param("vehicleId") Long vehicleId);
    
    /**
     * Delete all recall links for a specific recall
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM VehicleRecallLink vrl WHERE vrl.id.recallId = :recallId")
    void deleteByRecallId(@Param("recallId") Long recallId);
    
    /**
     * Find open (unrepaired) recalls for a vehicle
     */
    @Query("SELECT vrl FROM VehicleRecallLink vrl WHERE vrl.id.vehicleId = :vehicleId AND vrl.repairedFlag = false")
    List<VehicleRecallLink> findOpenRecallsForVehicle(@Param("vehicleId") Long vehicleId);
    
    /**
     * Find repaired recalls for a vehicle
     */
    @Query("SELECT vrl FROM VehicleRecallLink vrl WHERE vrl.id.vehicleId = :vehicleId AND vrl.repairedFlag = true")
    List<VehicleRecallLink> findRepairedRecallsForVehicle(@Param("vehicleId") Long vehicleId);
    
    /**
     * Mark a recall as repaired
     */
    @Modifying
    @Transactional
    @Query("UPDATE VehicleRecallLink vrl SET vrl.repairedFlag = true, vrl.repairDate = CURRENT_DATE WHERE vrl.id.vehicleId = :vehicleId AND vrl.id.recallId = :recallId")
    void markRecallAsRepaired(@Param("vehicleId") Long vehicleId, @Param("recallId") Long recallId);
    
    /**
     * Check if a vehicle has any open recalls
     */
    @Query("SELECT COUNT(vrl) > 0 FROM VehicleRecallLink vrl WHERE vrl.id.vehicleId = :vehicleId AND vrl.repairedFlag = false")
    boolean hasOpenRecalls(@Param("vehicleId") Long vehicleId);
}