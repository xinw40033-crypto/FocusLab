package com.focuslab.training.validator.impl;

import com.focuslab.training.validator.TrainingValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 数字重复查找验证器
 * 验证用户找到的重复数字位置是否正确
 */
@Component
public class NumberRepetitionValidator implements TrainingValidator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean validate(List<Object> userAnswers, String config) {
        try {
            // 解析配置JSON
            JsonNode configNode = objectMapper.readTree(config);
            
            // 获取正确的重复数字位置信息
            JsonNode rowsNode = configNode.get("rows");
            if (rowsNode == null || !rowsNode.isArray()) {
                return false;
            }
            
            // 构建正确答案映射
            Map<Integer, List<Integer>> correctAnswers = new HashMap<>();
            for (JsonNode row : rowsNode) {
                if (row.isArray()) {
                    for (JsonNode item : row) {
                        JsonNode valNode = item.get("val");
                        JsonNode posNode = item.get("pos");
                        
                        if (valNode != null && posNode != null && posNode.isArray()) {
                            int value = valNode.asInt();
                            int position = posNode.get(0).asInt(); // 假设位置信息在第一个元素中
                            
                            correctAnswers.computeIfAbsent(value, k -> new java.util.ArrayList<>()).add(position);
                        }
                    }
                }
            }
            
            // 验证用户答案
            if (userAnswers == null || userAnswers.isEmpty()) {
                return false;
            }
            
            // 解析用户提交的答案
            for (Object answerObj : userAnswers) {
                if (!(answerObj instanceof Map)) {
                    continue;
                }
                
                Map<?, ?> answerMap = (Map<?, ?>) answerObj;
                Integer value = (Integer) answerMap.get("value");
                Integer position = (Integer) answerMap.get("position");
                
                if (value != null && position != null) {
                    List<Integer> expectedPositions = correctAnswers.get(value);
                    if (expectedPositions == null || !expectedPositions.contains(position)) {
                        return false;
                    }
                }
            }
            
            // 检查用户是否找到了所有重复项
            int totalExpectedItems = 0;
            for (List<Integer> positions : correctAnswers.values()) {
                totalExpectedItems += positions.size();
            }
            
            return userAnswers.size() == totalExpectedItems;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}