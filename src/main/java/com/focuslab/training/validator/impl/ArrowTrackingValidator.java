package com.focuslab.training.validator.impl;

import com.focuslab.training.validator.TrainingValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 箭头追踪验证器
 * 验证用户点击的路径是否与预设路径一致
 */
@Component
public class ArrowTrackingValidator implements TrainingValidator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean validate(List<Object> userAnswers, String config) {
        try {
            // 解析配置JSON
            JsonNode configNode = objectMapper.readTree(config);
            
            // 获取预设路径
            JsonNode pathNode = configNode.get("path");
            if (pathNode == null || !pathNode.isArray()) {
                return false;
            }
            
            // 获取用户点击路径
            if (userAnswers == null || userAnswers.size() != pathNode.size()) {
                return false;
            }
            
            // 验证路径点是否一致（允许±3px的容差）
            for (int i = 0; i < pathNode.size(); i++) {
                JsonNode expectedPoint = pathNode.get(i);
                if (expectedPoint == null || !expectedPoint.isArray() || expectedPoint.size() < 2) {
                    return false;
                }
                
                // 获取预期坐标
                double expectedX = expectedPoint.get(0).asDouble();
                double expectedY = expectedPoint.get(1).asDouble();
                
                // 获取用户点击坐标
                Object userAnswer = userAnswers.get(i);
                if (!(userAnswer instanceof List)) {
                    return false;
                }
                
                List<?> userPoint = (List<?>) userAnswer;
                if (userPoint.size() < 2) {
                    return false;
                }
                
                double actualX, actualY;
                if (userPoint.get(0) instanceof Integer) {
                    actualX = ((Integer) userPoint.get(0)).doubleValue();
                } else {
                    actualX = (Double) userPoint.get(0);
                }
                
                if (userPoint.get(1) instanceof Integer) {
                    actualY = ((Integer) userPoint.get(1)).doubleValue();
                } else {
                    actualY = (Double) userPoint.get(1);
                }
                
                // 检查坐标是否在容差范围内
                if (Math.abs(actualX - expectedX) > 3 || Math.abs(actualY - expectedY) > 3) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}