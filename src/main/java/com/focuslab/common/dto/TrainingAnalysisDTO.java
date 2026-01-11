package com.focuslab.common.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 训练分析结果DTO
 */
public class TrainingAnalysisDTO {
    
    // 单次训练结果
    private Long recordId;
    private String trainingType;
    private Integer difficultyLevel;
    private Double score;
    private Double accuracyRate;
    private Integer timeSpent;
    private Long averageResponseTime;
    private Integer errorCount;
    private LocalDateTime completedAt;
    
    // 对比数据
    private ComparisonData comparison;
    
    // 趋势数据
    private TrendData trend;
    
    // 改善评估
    private ImprovementData improvement;

    // 内部类：对比数据
    public static class ComparisonData {
        private Double scoreDiff; // 得分变化
        private Double accuracyDiff; // 准确率变化
        private Integer timeDiff; // 用时变化
        private String performanceLevel; // 表现水平：improving/stable/declining
        
        public ComparisonData() {}
        
        public ComparisonData(Double scoreDiff, Double accuracyDiff, Integer timeDiff, String performanceLevel) {
            this.scoreDiff = scoreDiff;
            this.accuracyDiff = accuracyDiff;
            this.timeDiff = timeDiff;
            this.performanceLevel = performanceLevel;
        }

        public Double getScoreDiff() { return scoreDiff; }
        public void setScoreDiff(Double scoreDiff) { this.scoreDiff = scoreDiff; }
        public Double getAccuracyDiff() { return accuracyDiff; }
        public void setAccuracyDiff(Double accuracyDiff) { this.accuracyDiff = accuracyDiff; }
        public Integer getTimeDiff() { return timeDiff; }
        public void setTimeDiff(Integer timeDiff) { this.timeDiff = timeDiff; }
        public String getPerformanceLevel() { return performanceLevel; }
        public void setPerformanceLevel(String performanceLevel) { this.performanceLevel = performanceLevel; }
    }

    // 内部类：趋势数据
    public static class TrendData {
        private Double dailyAvgScore; // 日均分
        private Double weeklyAvgScore; // 周均分
        private Double monthlyAvgScore; // 月均分
        private Integer dailyTrainingCount; // 今日训练次数
        private Integer weeklyTrainingCount; // 本周训练次数
        private Integer monthlyTrainingCount; // 本月训练次数
        private List<ScorePoint> scoreHistory; // 历史分数点
        
        public TrendData() {}

        public Double getDailyAvgScore() { return dailyAvgScore; }
        public void setDailyAvgScore(Double dailyAvgScore) { this.dailyAvgScore = dailyAvgScore; }
        public Double getWeeklyAvgScore() { return weeklyAvgScore; }
        public void setWeeklyAvgScore(Double weeklyAvgScore) { this.weeklyAvgScore = weeklyAvgScore; }
        public Double getMonthlyAvgScore() { return monthlyAvgScore; }
        public void setMonthlyAvgScore(Double monthlyAvgScore) { this.monthlyAvgScore = monthlyAvgScore; }
        public Integer getDailyTrainingCount() { return dailyTrainingCount; }
        public void setDailyTrainingCount(Integer dailyTrainingCount) { this.dailyTrainingCount = dailyTrainingCount; }
        public Integer getWeeklyTrainingCount() { return weeklyTrainingCount; }
        public void setWeeklyTrainingCount(Integer weeklyTrainingCount) { this.weeklyTrainingCount = weeklyTrainingCount; }
        public Integer getMonthlyTrainingCount() { return monthlyTrainingCount; }
        public void setMonthlyTrainingCount(Integer monthlyTrainingCount) { this.monthlyTrainingCount = monthlyTrainingCount; }
        public List<ScorePoint> getScoreHistory() { return scoreHistory; }
        public void setScoreHistory(List<ScorePoint> scoreHistory) { this.scoreHistory = scoreHistory; }
    }

    // 历史分数点
    public static class ScorePoint {
        private LocalDateTime time;
        private Double score;
        
        public ScorePoint() {}
        
        public ScorePoint(LocalDateTime time, Double score) {
            this.time = time;
            this.score = score;
        }

        public LocalDateTime getTime() { return time; }
        public void setTime(LocalDateTime time) { this.time = time; }
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
    }

    // 内部类：改善评估
    public static class ImprovementData {
        private Double attentionImprovement; // 注意力改善指数 0-100
        private Double stabilityIndex; // 稳定性指数 0-100
        private String overallAssessment; // 总体评估：excellent/good/fair/poor
        private List<String> suggestions; // 改进建议
        
        public ImprovementData() {}

        public Double getAttentionImprovement() { return attentionImprovement; }
        public void setAttentionImprovement(Double attentionImprovement) { this.attentionImprovement = attentionImprovement; }
        public Double getStabilityIndex() { return stabilityIndex; }
        public void setStabilityIndex(Double stabilityIndex) { this.stabilityIndex = stabilityIndex; }
        public String getOverallAssessment() { return overallAssessment; }
        public void setOverallAssessment(String overallAssessment) { this.overallAssessment = overallAssessment; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    }

    // Getters and Setters
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }
    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Double getAccuracyRate() { return accuracyRate; }
    public void setAccuracyRate(Double accuracyRate) { this.accuracyRate = accuracyRate; }
    public Integer getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Integer timeSpent) { this.timeSpent = timeSpent; }
    public Long getAverageResponseTime() { return averageResponseTime; }
    public void setAverageResponseTime(Long averageResponseTime) { this.averageResponseTime = averageResponseTime; }
    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public ComparisonData getComparison() { return comparison; }
    public void setComparison(ComparisonData comparison) { this.comparison = comparison; }
    public TrendData getTrend() { return trend; }
    public void setTrend(TrendData trend) { this.trend = trend; }
    public ImprovementData getImprovement() { return improvement; }
    public void setImprovement(ImprovementData improvement) { this.improvement = improvement; }
}
