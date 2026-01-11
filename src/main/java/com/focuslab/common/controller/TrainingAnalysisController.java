package com.focuslab.common.controller;

import com.focuslab.common.dto.ApiResponse;
import com.focuslab.common.dto.TrainingAnalysisDTO;
import com.focuslab.common.entity.TrainingRecord;
import com.focuslab.common.repository.TrainingRecordRepository;
import com.focuslab.common.service.TrainingAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin
public class TrainingAnalysisController {

    @Autowired
    private TrainingAnalysisService analysisService;
    
    @Autowired
    private TrainingRecordRepository trainingRecordRepository;

    /**
     * 分析单次训练结果
     */
    @GetMapping("/training/{recordId}")
    public ApiResponse<TrainingAnalysisDTO> analyzeTraining(
            @PathVariable Long recordId,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.failure("未登录");
        }

        TrainingAnalysisDTO analysis = analysisService.analyzeTraining(recordId, userId);
        if (analysis == null) {
            return ApiResponse.failure("训练记录不存在");
        }

        return ApiResponse.success("分析完成", analysis);
    }

    /**
     * 获取用户训练趋势
     */
    @GetMapping("/trend")
    public ApiResponse<TrainingAnalysisDTO.TrendData> getTrend(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.failure("未登录");
        }

        TrainingAnalysisDTO.TrendData trend = analysisService.getTrendData(userId);
        return ApiResponse.success("获取成功", trend);
    }

    /**
     * 获取改善评估
     */
    @GetMapping("/improvement")
    public ApiResponse<TrainingAnalysisDTO.ImprovementData> getImprovement(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.failure("未登录");
        }

        TrainingAnalysisDTO.ImprovementData improvement = analysisService.assessImprovement(userId);
        return ApiResponse.success("评估完成", improvement);
    }

    /**
     * 获取时间段内的训练记录
     */
    @GetMapping("/records/{period}")
    public ApiResponse<Map<String, Object>> getRecordsByPeriod(
            @PathVariable String period,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.failure("未登录");
        }

        List<TrainingRecord> records = analysisService.getRecordsByPeriod(userId, period);
        
        Map<String, Object> result = new HashMap<>();
        result.put("period", period);
        result.put("count", records.size());
        result.put("records", records);

        return ApiResponse.success("获取成功", result);
    }

    /**
     * 获取综合报告（包含所有分析数据）
     */
    @GetMapping("/report")
    public ApiResponse<Map<String, Object>> getComprehensiveReport(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.failure("未登录");
        }

        Map<String, Object> report = new HashMap<>();
        
        // 趋势数据
        report.put("trend", analysisService.getTrendData(userId));
        
        // 改善评估
        report.put("improvement", analysisService.assessImprovement(userId));
        
        // 各时间段记录
        report.put("dailyRecords", analysisService.getRecordsByPeriod(userId, "day"));
        report.put("weeklyRecords", analysisService.getRecordsByPeriod(userId, "week"));
        report.put("monthlyRecords", analysisService.getRecordsByPeriod(userId, "month"));

        return ApiResponse.success("报告生成成功", report);
    }
    
    /**
     * 获取用户所有训练记录（分页）
     */
    @GetMapping("/records/all")
    public ApiResponse<Map<String, Object>> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.failure("未登录");
        }
        
        Page<TrainingRecord> recordPage = trainingRecordRepository.findByUserIdOrderByCompletedAtDesc(
            userId, 
            PageRequest.of(page, size)
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", recordPage.getContent());
        result.put("total", recordPage.getTotalElements());
        result.put("page", page);
        result.put("size", size);
        
        return ApiResponse.success("获取成功", result);
    }
}
