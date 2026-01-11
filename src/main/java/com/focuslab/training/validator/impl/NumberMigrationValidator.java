package com.focuslab.training.validator.impl;

import com.focuslab.training.validator.TrainingValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 数字迁移验证器
 * 验证用户对数字移动路径的判断是否正确
 */
@Component
public class NumberMigrationValidator implements TrainingValidator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean validate(List<Object> userAnswers, String config) {
        try {
            // 解析配置JSON
            JsonNode configNode = objectMapper.readTree(config);
            
            // 获取正确迁移路径信息
            JsonNode migrationsNode = configNode.get("migrations");
            if (migrationsNode == null || !migrationsNode.isArray()) {
                return false;
            }
            
            // 构建正确答案映射
            Map<String, Object> correctAnswers = new HashMap<>();
            for (JsonNode migrationNode : migrationsNode) {
                JsonNode startNode = migrationNode.get("start");
                JsonNode endNode = migrationNode.get("end");
                JsonNode numberNode = migrationNode.get("number");
                
                if (startNode != null && endNode != null && numberNode != null) {
                    String start = startNode.asText(); // 起始位置
                    String end = endNode.asText();     // 结束位置
                    Object number = parseJsonValue(numberNode);
                    
                    // 使用起始位置作为键
                    String key = start + "_to_" + end;
                    correctAnswers.put(key, number);
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
                String start = (String) answerMap.get("start");
                String end = (String) answerMap.get("end");
                Object userNumber = answerMap.get("number");
                
                if (start != null && end != null) {
                    String key = start + "_to_" + end;
                    Object expectedNumber = correctAnswers.get(key);
                    
                    if (expectedNumber == null) {
                        return false; // 用户指定了不存在的迁移路径
                    }
                    
                    // 比较数字是否相等
                    if (!valuesEqual(expectedNumber, userNumber)) {
                        return false;
                    }
                }
            }
            
            // 检查用户是否回答了所有迁移路径
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