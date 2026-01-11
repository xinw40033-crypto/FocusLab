package com.focuslab.training.validator.impl;

import com.focuslab.training.validator.TrainingValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 动物数字匹配验证器
 * 验证用户对动物与数字的匹配是否正确
 */
@Component
public class AnimalNumberMatchValidator implements TrainingValidator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean validate(List<Object> userAnswers, String config) {
        try {
            // 解析配置JSON（配置是一个数组，包含动物、位置和目标数字）
            JsonNode configNode = objectMapper.readTree(config);
            
            if (!configNode.isArray()) {
                return false;
            }
            
            // 构建正确答案映射
            Map<String, Integer> correctAnswers = new HashMap<>();
            for (JsonNode item : configNode) {
                JsonNode animalNode = item.get("animal");
                JsonNode targetNode = item.get("target");
                
                if (animalNode != null && targetNode != null) {
                    String animal = animalNode.asText();
                    int target = targetNode.asInt();
                    
                    correctAnswers.put(animal, target);
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
                
                @SuppressWarnings("unchecked")
                Map<String, Object> answerMap = (Map<String, Object>) answerObj;
                String animal = (String) answerMap.get("animal");
                Object targetObj = answerMap.get("target");
                
                Integer targetValue;
                if (targetObj instanceof Integer) {
                    targetValue = (Integer) targetObj;
                } else if (targetObj instanceof Double) {
                    targetValue = ((Double) targetObj).intValue();
                } else {
                    continue;
                }
                
                if (animal != null) {
                    Integer expectedTarget = correctAnswers.get(animal);
                    if (expectedTarget == null || !expectedTarget.equals(targetValue)) {
                        // 允许±1的容错
                        if (expectedTarget != null && Math.abs(expectedTarget - targetValue) > 1) {
                            return false;
                        }
                    }
                }
            }
            
            // 检查用户是否回答了所有问题
            return userAnswers.size() == correctAnswers.size();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}