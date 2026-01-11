package com.focuslab.training.validator.impl;

import com.focuslab.training.validator.TrainingValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 舒尔特方格验证器
 * 验证用户按顺序点击数字的路径是否正确
 */
@Component
public class SchultzeTableValidator implements TrainingValidator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean validate(List<Object> userAnswers, String config) {
        try {
            // 解析配置JSON
            JsonNode configNode = objectMapper.readTree(config);
            
            // 获取正确数字序列信息
            JsonNode sequenceNode = configNode.get("sequence");
            if (sequenceNode == null || !sequenceNode.isArray()) {
                return false;
            }
            
            // 构建正确序列映射（数字 -> 应该被点击的顺序）
            Map<Integer, Integer> correctSequence = new HashMap<>();
            for (int i = 0; i < sequenceNode.size(); i++) {
                JsonNode numNode = sequenceNode.get(i);
                if (numNode != null) {
                    int number = numNode.asInt();
                    correctSequence.put(number, i); // 数字应该在第几个被点击
                }
            }
            
            // 验证用户答案
            if (userAnswers == null || userAnswers.isEmpty()) {
                return false;
            }
            
            // 验证用户点击的顺序是否正确
            for (int i = 0; i < userAnswers.size(); i++) {
                Object answerObj = userAnswers.get(i);
                
                if (answerObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> answerMap = (Map<String, Object>) answerObj;
                    Object numberObj = answerMap.get("number");
                    
                    Integer number;
                    if (numberObj instanceof Integer) {
                        number = (Integer) numberObj;
                    } else if (numberObj instanceof Double) {
                        number = ((Double) numberObj).intValue();
                    } else {
                        continue;
                    }
                    
                    // 检查这个数字是否应该在这个位置被点击
                    Integer expectedPosition = correctSequence.get(number);
                    if (expectedPosition == null || !expectedPosition.equals(i)) {
                        return false;
                    }
                } else if (answerObj instanceof Integer) {
                    // 如果答案直接是数字
                    Integer number = (Integer) answerObj;
                    Integer expectedPosition = correctSequence.get(number);
                    if (expectedPosition == null || !expectedPosition.equals(i)) {
                        return false;
                    }
                } else if (answerObj instanceof Double) {
                    // 如果答案是double类型的数字
                    Integer number = ((Double) answerObj).intValue();
                    Integer expectedPosition = correctSequence.get(number);
                    if (expectedPosition == null || !expectedPosition.equals(i)) {
                        return false;
                    }
                }
            }
            
            // 检查用户是否点击了所有数字
            return userAnswers.size() == correctSequence.size();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}