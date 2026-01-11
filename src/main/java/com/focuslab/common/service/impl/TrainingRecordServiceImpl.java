package com.focuslab.common.service.impl;

import com.focuslab.common.dto.SubmissionDto;
import com.focuslab.common.entity.TrainingRecord;
import com.focuslab.common.repository.TrainingRecordRepository;
import com.focuslab.common.service.TrainingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TrainingRecordServiceImpl implements TrainingRecordService {

    @Autowired
    private TrainingRecordRepository trainingRecordRepository;

    @Override
    public void saveRecord(Long userId, Long contentId, double score, Long totalTimeSpent) {
        TrainingRecord record = new TrainingRecord();
        record.setUserId(userId);
        record.setContentId(contentId);
        record.setScore(score);
        record.setAverageResponseTime(totalTimeSpent);
        
        trainingRecordRepository.save(record);
    }

    @Override
    public Object getTrainingReport(Long userId) {
        // 获取用户的平均得分
        Double avgScore = trainingRecordRepository.findAverageScoreByUserId(userId);
        
        // 获取用户的平均反应时间
        Double avgResponseTime = trainingRecordRepository.findAverageResponseTimeByUserId(userId);
        
        // 获取用户总训练次数
        int totalTrainings = trainingRecordRepository.findByUserId(userId).size();
        
        // 构建报告数据
        Map<String, Object> report = new HashMap<>();
        report.put("userId", userId);
        report.put("totalTrainings", totalTrainings);
        report.put("averageScore", avgScore != null ? avgScore : 0.0);
        report.put("averageResponseTime", avgResponseTime != null ? avgResponseTime : 0.0);
        report.put("improvementTrend", calculateImprovementTrend(userId)); // 简化的进步趋势
        
        return report;
    }

    // 计算进步趋势
    private String calculateImprovementTrend(Long userId) {
        // 获取最近10次训练记录
        List<TrainingRecord> recentRecords = trainingRecordRepository.findTop10ByUserIdOrderByCompletedAtDesc(userId);
        
        if (recentRecords.size() < 3) {
            return "STABLE"; // 数据不足，返回稳定
        }
        
        // 将记录分为前半部分和后半部分
        int halfSize = recentRecords.size() / 2;
        List<TrainingRecord> recentHalf = recentRecords.subList(0, halfSize);
        List<TrainingRecord> olderHalf = recentRecords.subList(halfSize, recentRecords.size());
        
        // 计算两部分的平均得分
        double recentAvg = recentHalf.stream()
            .mapToDouble(TrainingRecord::getScore)
            .average()
            .orElse(0.0);
            
        double olderAvg = olderHalf.stream()
            .mapToDouble(TrainingRecord::getScore)
            .average()
            .orElse(0.0);
        
        // 判断趋势：差值大于5分才认为有明显变化
        double diff = recentAvg - olderAvg;
        if (diff > 5) {
            return "UPWARD"; // 上升
        } else if (diff < -5) {
            return "DOWNWARD"; // 下降
        } else {
            return "STABLE"; // 稳定
        }
    }

    @Override
    public Long saveDetailedRecord(SubmissionDto dto) {
        TrainingRecord record = new TrainingRecord();
        record.setUserId(dto.getUserId());
        record.setContentId(dto.getContentId());
        record.setTrainingType(dto.getTrainingType());
        record.setDifficultyLevel(dto.getDifficultyLevel());
        record.setScore(dto.getScore());
        record.setTimeSpent(dto.getTimeSpent());
        record.setCorrectCount(dto.getCorrectCount());
        record.setTotalCount(dto.getTotalCount());
        record.setErrorCount(dto.getErrorCount());
        record.setAccuracyRate(dto.getAccuracyRate());
        record.setAverageResponseTime(dto.getAverageResponseTime());
        record.setInteractionData(dto.getInteractionData());
        record.setStartedAt(java.time.LocalDateTime.now().minusSeconds(dto.getTimeSpent()));
        
        TrainingRecord saved = trainingRecordRepository.save(record);
        return saved.getId();
    }
}