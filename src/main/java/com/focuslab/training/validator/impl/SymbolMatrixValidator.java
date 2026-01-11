package com.focuslab.training.validator.impl;

import com.focuslab.training.validator.TrainingValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 符号矩阵验证器
 * 验证用户对符号模式的识别是否正确
 */
@Component
public class SymbolMatrixValidator implements TrainingValidator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean validate(List<Object> userAnswers, String config) {
        try {
            // 解析配置JSON
            JsonNode configNode = objectMapper.readTree(config);
            
            // 获取正确答案信息
            JsonNode targetsNode = configNode.get("targets");
            if (targetsNode == null || !targetsNode.isArray()) {
                return false;
            }
            
            // 构建正确答案映射
            Map<String, Object> correctAnswers = new HashMap<>();
            for (JsonNode targetNode : targetsNode) {
                JsonNode positionNode = targetNode.get("position");
                JsonNode valueNode = targetNode.get("value");
                
                if (positionNode != null && valueNode != null) {
                    String position = positionNode.asText(); // 例如 "A1", "B2" 等
                    Object value = parseJsonValue(valueNode);
                    
                    correctAnswers.put(position, value);
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
                String position = (String) answerMap.get("position");
                Object userValue = answerMap.get("value");
                
                if (position != null) {
                    Object expectedValue = correctAnswers.get(position);
                    if (expectedValue == null) {
                        return false; // 用户指定了不存在的位置
                    }
                    
                    // 比较值是否相等
                    if (!valuesEqual(expectedValue, userValue)) {
                        return false;
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
    
    // 辅助方法：解析JSON值
    private Object parseJsonValue(JsonNode node) {
        if (node.isTextual()) {
            return node.asText();
        } else if (node.isNumber()) {
            if (node.isInt()) {
                return node.asInt();
            } else {
                return node.asDouble();
            }
        } else if (node.isBoolean()) {
            return node.asBoolean();
        } else {
            return node.toString();
        }
    }
    
    // 辅助方法：比较两个值是否相等
    private boolean valuesEqual(Object expected, Object actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        
        // 处理数值比较（考虑整数和浮点数之间的转换）
        if (expected instanceof Number && actual instanceof Number) {
            return ((Number) expected).doubleValue() == ((Number) actual).doubleValue();
        }
        
        return expected.equals(actual);
    }
}