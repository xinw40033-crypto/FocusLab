package com.focuslab.common.dto;

import java.util.List;

public class SubmissionDto {

    private Long userId;
    private Long contentId;
    private List<Object> answers; // 不同题型的答案格式不同，使用Object数组
    private Long totalTimeSpent; // 总用时（毫秒）
    
    // 新增字段
    private String trainingType; // 训练类型
    private Integer difficultyLevel; // 难度等级
    private Integer timeSpent; // 耗时（秒）
    private Double score; // 得分
    private Integer correctCount; // 正确数
    private Integer totalCount; // 总题数
    private Integer errorCount; // 错误次数
    private Double accuracyRate; // 准确率
    private Long averageResponseTime; // 平均反应时间
    private String interactionData; // 交互数据JSON

    // Constructors
    public SubmissionDto() {}

    public SubmissionDto(Long userId, Long contentId, List<Object> answers, Long totalTimeSpent) {
        this.userId = userId;
        this.contentId = contentId;
        this.answers = answers;
        this.totalTimeSpent = totalTimeSpent;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public List<Object> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Object> answers) {
        this.answers = answers;
    }

    public Long getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(Long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }
    
    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    
    public Integer getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Integer timeSpent) { this.timeSpent = timeSpent; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    
    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }
    
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    
    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }
    
    public Double getAccuracyRate() { return accuracyRate; }
    public void setAccuracyRate(Double accuracyRate) { this.accuracyRate = accuracyRate; }
    
    public Long getAverageResponseTime() { return averageResponseTime; }
    public void setAverageResponseTime(Long averageResponseTime) { this.averageResponseTime = averageResponseTime; }
    
    public String getInteractionData() { return interactionData; }
    public void setInteractionData(String interactionData) { this.interactionData = interactionData; }
}