package com.focuslab.common.service.impl;

import com.focuslab.common.dto.TrainingAnalysisDTO;
import com.focuslab.common.entity.TrainingRecord;
import com.focuslab.common.repository.TrainingRecordRepository;
import com.focuslab.common.service.TrainingAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingAnalysisServiceImpl implements TrainingAnalysisService {

    @Autowired
    private TrainingRecordRepository trainingRecordRepository;

    @Override
    public TrainingAnalysisDTO analyzeTraining(Long recordId, Long userId) {
        TrainingRecord record = trainingRecordRepository.findById(recordId).orElse(null);
        if (record == null) {
            return null;
        }

        TrainingAnalysisDTO analysis = new TrainingAnalysisDTO();
        
        // 基础信息
        analysis.setRecordId(record.getId());
        analysis.setTrainingType(record.getTrainingType());
        analysis.setDifficultyLevel(record.getDifficultyLevel());
        analysis.setScore(record.getScore());
        analysis.setAccuracyRate(record.getAccuracyRate());
        analysis.setTimeSpent(record.getTimeSpent());
        analysis.setAverageResponseTime(record.getAverageResponseTime());
        analysis.setErrorCount(record.getErrorCount());
        analysis.setCompletedAt(record.getCompletedAt());
        
        // 对比数据
        analysis.setComparison(compareWithLastTraining(record, userId));
        
        // 趋势数据
        analysis.setTrend(getTrendData(userId));
        
        // 改善评估
        analysis.setImprovement(assessImprovement(userId));
        
        return analysis;
    }

    @Override
    public TrainingAnalysisDTO.ComparisonData compareWithLastTraining(TrainingRecord current, Long userId) {
        // 查找同类型的上一次训练
        List<TrainingRecord> previous = trainingRecordRepository.findPreviousTraining(
            userId, 
            current.getTrainingType(), 
            current.getId(), 
            PageRequest.of(0, 1)
        );

        if (previous.isEmpty()) {
            return new TrainingAnalysisDTO.ComparisonData(0.0, 0.0, 0, "first");
        }

        TrainingRecord last = previous.get(0);
        
        Double scoreDiff = current.getScore() - last.getScore();
        Double accuracyDiff = (current.getAccuracyRate() != null && last.getAccuracyRate() != null) 
            ? current.getAccuracyRate() - last.getAccuracyRate() 
            : 0.0;
        Integer timeDiff = (current.getTimeSpent() != null && last.getTimeSpent() != null)
            ? current.getTimeSpent() - last.getTimeSpent()
            : 0;

        // 判断表现水平
        String performanceLevel;
        if (scoreDiff > 5 || (accuracyDiff > 5 && timeDiff < 0)) {
            performanceLevel = "improving";
        } else if (scoreDiff < -5 || (accuracyDiff < -5 || timeDiff > 10)) {
            performanceLevel = "declining";
        } else {
            performanceLevel = "stable";
        }

        return new TrainingAnalysisDTO.ComparisonData(scoreDiff, accuracyDiff, timeDiff, performanceLevel);
    }

    @Override
    public TrainingAnalysisDTO.TrendData getTrendData(Long userId) {
        TrainingAnalysisDTO.TrendData trend = new TrainingAnalysisDTO.TrendData();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime weekStart = now.minus(7, ChronoUnit.DAYS);
        LocalDateTime monthStart = now.minus(30, ChronoUnit.DAYS);
        
        // 今日记录
        List<TrainingRecord> dailyRecords = trainingRecordRepository
            .findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(userId, todayStart, now);
        trend.setDailyTrainingCount(dailyRecords.size());
        trend.setDailyAvgScore(calculateAvgScore(dailyRecords));
        
        // 本周记录
        List<TrainingRecord> weeklyRecords = trainingRecordRepository
            .findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(userId, weekStart, now);
        trend.setWeeklyTrainingCount(weeklyRecords.size());
        trend.setWeeklyAvgScore(calculateAvgScore(weeklyRecords));
        
        // 本月记录
        List<TrainingRecord> monthlyRecords = trainingRecordRepository
            .findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(userId, monthStart, now);
        trend.setMonthlyTrainingCount(monthlyRecords.size());
        trend.setMonthlyAvgScore(calculateAvgScore(monthlyRecords));
        
        // 历史分数点（最近10次）
        List<TrainingRecord> recentRecords = trainingRecordRepository.findTop10ByUserIdOrderByCompletedAtDesc(userId);
        List<TrainingAnalysisDTO.ScorePoint> scoreHistory = recentRecords.stream()
            .map(r -> new TrainingAnalysisDTO.ScorePoint(r.getCompletedAt(), r.getScore()))
            .collect(Collectors.toList());
        trend.setScoreHistory(scoreHistory);
        
        return trend;
    }

    @Override
    public TrainingAnalysisDTO.ImprovementData assessImprovement(Long userId) {
        TrainingAnalysisDTO.ImprovementData improvement = new TrainingAnalysisDTO.ImprovementData();
        
        // 获取最近30天的记录
        LocalDateTime monthStart = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        List<TrainingRecord> records = trainingRecordRepository
            .findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(userId, monthStart, LocalDateTime.now());
        
        if (records.size() < 5) {
            improvement.setAttentionImprovement(0.0);
            improvement.setStabilityIndex(0.0);
            improvement.setOverallAssessment("insufficient_data");
            improvement.setSuggestions(List.of("需要更多训练数据来评估改善情况"));
            return improvement;
        }
        
        // 计算注意力改善指数（对比前后期平均分）
        int halfSize = records.size() / 2;
        List<TrainingRecord> recentHalf = records.subList(0, halfSize);
        List<TrainingRecord> olderHalf = records.subList(halfSize, records.size());
        
        double recentAvg = calculateAvgScore(recentHalf);
        double olderAvg = calculateAvgScore(olderHalf);
        double improvementRate = ((recentAvg - olderAvg) / olderAvg) * 100;
        improvement.setAttentionImprovement(Math.max(0, Math.min(100, 50 + improvementRate)));
        
        // 计算稳定性指数（标准差）
        double avgScore = calculateAvgScore(records);
        double variance = records.stream()
            .mapToDouble(r -> Math.pow(r.getScore() - avgScore, 2))
            .average()
            .orElse(0.0);
        double stdDev = Math.sqrt(variance);
        double stabilityIndex = Math.max(0, 100 - stdDev * 2);
        improvement.setStabilityIndex(stabilityIndex);
        
        // 总体评估
        String assessment;
        if (recentAvg >= 90 && improvementRate > 0) {
            assessment = "excellent";
        } else if (recentAvg >= 80 && improvementRate >= -5) {
            assessment = "good";
        } else if (recentAvg >= 70) {
            assessment = "fair";
        } else {
            assessment = "poor";
        }
        improvement.setOverallAssessment(assessment);
        
        // 改进建议
        List<String> suggestions = new ArrayList<>();
        if (recentAvg < 80) {
            suggestions.add("建议增加训练频率，每天至少完成2次训练");
        }
        if (stabilityIndex < 70) {
            suggestions.add("表现不够稳定，建议选择固定时间段进行训练");
        }
        if (improvementRate < 0) {
            suggestions.add("近期成绩有所下降，建议调整训练难度或休息调整");
        }
        if (suggestions.isEmpty()) {
            suggestions.add("保持当前训练节奏，继续加油！");
        }
        improvement.setSuggestions(suggestions);
        
        return improvement;
    }

    @Override
    public List<TrainingRecord> getRecordsByPeriod(Long userId, String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        
        switch (period.toLowerCase()) {
            case "day":
                start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                break;
            case "week":
                start = now.minus(7, ChronoUnit.DAYS);
                break;
            case "month":
                start = now.minus(30, ChronoUnit.DAYS);
                break;
            default:
                start = now.minus(30, ChronoUnit.DAYS);
        }
        
        return trainingRecordRepository.findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(userId, start, now);
    }

    private Double calculateAvgScore(List<TrainingRecord> records) {
        if (records.isEmpty()) {
            return 0.0;
        }
        return records.stream()
            .mapToDouble(TrainingRecord::getScore)
            .average()
            .orElse(0.0);
    }
}
