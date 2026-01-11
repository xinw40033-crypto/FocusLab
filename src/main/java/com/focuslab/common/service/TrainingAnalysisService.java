package com.focuslab.common.service;

import com.focuslab.common.dto.TrainingAnalysisDTO;
import com.focuslab.common.entity.TrainingRecord;

import java.util.List;

/**
 * 训练数据分析服务接口
 */
public interface TrainingAnalysisService {
    
    /**
     * 分析单次训练结果（包含对比、趋势、改善评估）
     */
    TrainingAnalysisDTO analyzeTraining(Long recordId, Long userId);
    
    /**
     * 对比当前训练与上次训练
     */
    TrainingAnalysisDTO.ComparisonData compareWithLastTraining(TrainingRecord current, Long userId);
    
    /**
     * 获取用户训练趋势（日/周/月）
     */
    TrainingAnalysisDTO.TrendData getTrendData(Long userId);
    
    /**
     * 评估用户注意力改善情况
     */
    TrainingAnalysisDTO.ImprovementData assessImprovement(Long userId);
    
    /**
     * 获取用户某段时间的训练记录
     */
    List<TrainingRecord> getRecordsByPeriod(Long userId, String period); // period: day/week/month
}
