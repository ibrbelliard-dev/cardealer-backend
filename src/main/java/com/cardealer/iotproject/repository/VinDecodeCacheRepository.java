package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.VinDecodeCache;
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
public interface VinDecodeCacheRepository extends JpaRepository<VinDecodeCache, String> {
    
    /**
     * Find cache entry by VIN
     */
    Optional<VinDecodeCache> findByVin(String vin);
    
    /**
     * Check if VIN exists in cache
     */
    boolean existsByVin(String vin);
    
    /**
     * Find cache entries older than specified date
     */
    List<VinDecodeCache> findByLastDecodedBefore(LocalDateTime dateTime);
    
    /**
     * Delete cache entries older than specified date
     */
    @Modifying
    @Transactional
    long deleteByLastDecodedBefore(LocalDateTime dateTime);
    
    /**
     * Find cache entries that haven't been accessed recently
     */
    @Query("SELECT v FROM VinDecodeCache v WHERE v.lastDecoded < :cutoffDate ORDER BY v.decodeCount ASC")
    List<VinDecodeCache> findStaleEntries(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Get total count of cached VINs
     */
    @Query("SELECT COUNT(v) FROM VinDecodeCache v")
    long getTotalCachedCount();
    
    /**
     * Get total size estimate (for cache statistics)
     */
    @Query("SELECT SUM(LENGTH(v.decodedData)) FROM VinDecodeCache v")
    Long getTotalCacheSize();
    
    /**
     * Find most frequently accessed cache entries
     */
    @Query("SELECT v FROM VinDecodeCache v ORDER BY v.decodeCount DESC")
    List<VinDecodeCache> findMostFrequent(int limit);
    
    /**
     * Increment decode count for a VIN
     */
    @Modifying
    @Transactional
    @Query("UPDATE VinDecodeCache v SET v.decodeCount = v.decodeCount + 1, v.lastDecoded = CURRENT_TIMESTAMP WHERE v.vin = :vin")
    void incrementDecodeCount(@Param("vin") String vin);
    
    /**
     * Clean up old cache entries (keep only recent ones)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM VinDecodeCache v WHERE v.lastDecoded < :cutoffDate AND v.decodeCount < :minAccessCount")
    int cleanOldCacheEntries(@Param("cutoffDate") LocalDateTime cutoffDate, 
                             @Param("minAccessCount") int minAccessCount);
    
    /**
     * Find all VINs in cache (for export or batch processing)
     */
    @Query("SELECT v.vin FROM VinDecodeCache v")
    List<String> findAllVins();
    
    /**
     * Get cache hit ratio - returns count of entries accessed more than once
     */
    @Query("SELECT COUNT(v) FROM VinDecodeCache v WHERE v.decodeCount > 1")
    long getCacheHitCount();
    
    /**
     * Get total decode count across all cached entries
     */
    @Query("SELECT SUM(v.decodeCount) FROM VinDecodeCache v")
    Long getTotalDecodeCount();
    
    /**
     * Find recently decoded VINs
     */
    @Query("SELECT v FROM VinDecodeCache v WHERE v.lastDecoded > :since ORDER BY v.lastDecoded DESC")
    List<VinDecodeCache> findRecentlyDecoded(@Param("since") LocalDateTime since);
    
    /**
     * Get cache statistics summary
     */
    @Query("SELECT new map(" +
           "COUNT(v) as totalEntries, " +
           "COALESCE(SUM(v.decodeCount), 0) as totalAccesses, " +
           "COALESCE(AVG(v.decodeCount), 0) as averageAccessCount, " +
           "MAX(v.lastDecoded) as newestEntry, " +
           "MIN(v.lastDecoded) as oldestEntry) " +
           "FROM VinDecodeCache v")
    List<Object[]> getCacheStatistics();
}