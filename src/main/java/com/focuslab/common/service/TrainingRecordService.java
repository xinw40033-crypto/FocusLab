package com.focuslab.common.service;

import com.focuslab.common.dto.SubmissionDto;

public interface TrainingRecordService {
    void saveRecord(Long userId, Long contentId, double score, Long totalTimeSpent);
    Object getTrainingReport(Long userId);
    
    /**
     * 保存详细的训练记录
     */
    Long saveDetailedRecord(SubmissionDto submissionDto);
}