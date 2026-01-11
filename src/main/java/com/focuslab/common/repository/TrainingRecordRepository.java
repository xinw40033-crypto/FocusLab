package com.focuslab.common.repository;

import com.focuslab.common.entity.TrainingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainingRecordRepository extends JpaRepository<TrainingRecord, Long> {
    List<TrainingRecord> findByUserId(Long userId);
    
    List<TrainingRecord> findByContentId(Long contentId);
    
    @Query("SELECT AVG(tr.score) FROM TrainingRecord tr WHERE tr.userId = :userId")
    Double findAverageScoreByUserId(@Param("userId") Long userId);
    
    @Query("SELECT AVG(tr.averageResponseTime) FROM TrainingRecord tr WHERE tr.userId = :userId")
    Double findAverageResponseTimeByUserId(@Param("userId") Long userId);
    
    // 按类型和时间查询
    List<TrainingRecord> findByUserIdAndTrainingTypeOrderByCompletedAtDesc(Long userId, String trainingType);
    
    // 按时间范围查询
    List<TrainingRecord> findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(Long userId, LocalDateTime start, LocalDateTime end);
    
    // 查找最近N条记录
    List<TrainingRecord> findTop10ByUserIdOrderByCompletedAtDesc(Long userId);
    
    // 分页查询用户记录（按时间倒序）
    Page<TrainingRecord> findByUserIdOrderByCompletedAtDesc(Long userId, Pageable pageable);
    
    // 查找同类型的上一次训练
    @Query("SELECT tr FROM TrainingRecord tr WHERE tr.userId = :userId AND tr.trainingType = :trainingType AND tr.id < :currentId ORDER BY tr.completedAt DESC")
    List<TrainingRecord> findPreviousTraining(@Param("userId") Long userId, @Param("trainingType") String trainingType, @Param("currentId") Long currentId, Pageable pageable);
}