package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {
    
    List<SyncLog> findBySyncTypeAndStatus(SyncLog.SyncType syncType, SyncLog.SyncStatus status);
    
    List<SyncLog> findByStartTimeAfter(LocalDateTime startTime);
    
    @Query("SELECT s FROM SyncLog s WHERE s.syncType = :syncType ORDER BY s.startTime DESC")
    SyncLog findTopBySyncTypeOrderByStartTimeDesc(@Param("syncType") SyncLog.SyncType syncType);
    
    @Query("SELECT s FROM SyncLog s WHERE s.status = :status ORDER BY s.startTime DESC")
    List<SyncLog> findLastSyncByStatus(@Param("status") SyncLog.SyncStatus status);
    
    @Query("SELECT COUNT(s) FROM SyncLog s WHERE s.status = 'RUNNING'")
    long countRunningSyncs();
    
    @Query("SELECT s FROM SyncLog s WHERE s.endTime IS NULL ORDER BY s.startTime DESC")
    List<SyncLog> findIncompleteSyncs();
}