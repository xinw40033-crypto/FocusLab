package com.focuslab.common.controller;

import com.focuslab.common.dto.Result;
import com.focuslab.common.dto.SubmissionDto;
import com.focuslab.common.service.TrainingRecordService;
import com.focuslab.training.validator.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TrainingSubmissionController {

    @Autowired
    private TrainingRecordService trainingRecordService;

    @Autowired
    private ValidatorFactory validatorFactory;

    @PostMapping("/submit")
    public ResponseEntity<Result<Object>> submitTraining(@RequestBody SubmissionDto submissionDto, HttpSession session) {
        try {
            // 从session获取当前登录用户ID，不信任前端传值
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.ok(Result.failure("未登录，请先登录"));
            }
            
            // 强制使用session中的userId，忽略前端传来的userId
            submissionDto.setUserId(userId);
            
            // 保存训练记录（包含详细数据）
            Long recordId = trainingRecordService.saveDetailedRecord(submissionDto);
            
            Map<String, Object> result = new HashMap<>();
            result.put("recordId", recordId);
            result.put("score", submissionDto.getScore());
            result.put("success", true);
            
            return ResponseEntity.ok(Result.success("训练完成！", result));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.failure("提交训练结果失败: " + e.getMessage()));
        }
    }

    // 根据验证结果和用时计算得分
    private double calculateScore(SubmissionDto submissionDto, boolean isValid) {
        if (!isValid) {
            return 0.0;
        }
        
        // 基础分为100分，根据用时调整
        double baseScore = 100.0;
        
        // 如果用时超过30秒，每多1秒扣1分
        if (submissionDto.getTotalTimeSpent() != null && submissionDto.getTotalTimeSpent() > 30000) {
            long extraTimeSeconds = (submissionDto.getTotalTimeSpent() - 30000) / 1000;
            baseScore = Math.max(0, baseScore - extraTimeSeconds);
        }
        
        return baseScore;
    }

    @GetMapping("/report/{userId}")
    public ResponseEntity<Result<Object>> getTrainingReport(@PathVariable Long userId, HttpSession session) {
        try {
            // 验证session，只能查询自己的报告（管理员除外）
            Long currentUserId = (Long) session.getAttribute("userId");
            String role = (String) session.getAttribute("role");
            
            if (currentUserId == null) {
                return ResponseEntity.ok(Result.failure("未登录，请先登录"));
            }
            
            // 非管理员只能查看自己的报告
            if (!"ADMIN".equals(role) && !currentUserId.equals(userId)) {
                return ResponseEntity.ok(Result.failure("无权访问其他用户的训练报告"));
            }
            
            Object report = trainingRecordService.getTrainingReport(userId);
            return ResponseEntity.ok(Result.success("获取训练报告成功", report));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.failure("获取训练报告失败: " + e.getMessage()));
        }
    }
    
    // 内部类用于提交结果
    private static class SubmissionResult {
        private boolean success;
        private double score;
        private String message;

        public SubmissionResult(boolean success, double score, String message) {
            this.success = success;
            this.score = score;
            this.message = message;
        }

        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}