package com.focuslab.training.validator.impl;

import com.focuslab.training.validator.TrainingValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 数字对比验证器
 * 验证用户对数字大小关系的判断是否正确
 */
@Component
public class NumberCompareValidator implements TrainingValidator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean validate(List<Object> userAnswers, String config) {
        try {
            // 解析配置JSON
            JsonNode configNode = objectMapper.readTree(config);
            
            // 获取正确答案信息
            JsonNode pairsNode = configNode.get("pairs");
            if (pairsNode == null || !pairsNode.isArray()) {
                return false;
            }
            
            // 构建正确答案映射
            Map<String, Boolean> correctAnswers = new HashMap<>();
            for (JsonNode pairNode : pairsNode) {
                JsonNode num1Node = pairNode.get("num1");
                JsonNode num2Node = pairNode.get("num2");
                JsonNode isGreaterNode = pairNode.get("isGreater"); // 是否第一个数大于第二个数
                
                if (num1Node != null && num2Node != null && isGreaterNode != null) {
                    int num1 = num1Node.asInt();
                    int num2 = num2Node.asInt();
                    boolean isGreater = isGreaterNode.asBoolean();
                    
                    // 使用数字对作为键
                    String key = num1 + "_" + num2;
                    correctAnswers.put(key, isGreater);
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
                Object num1Obj = answerMap.get("num1");
                Object num2Obj = answerMap.get("num2");
                Object userChoiceObj = answerMap.get("choice");
                
                Integer num1, num2;
                Boolean userChoice;
                
                if (num1Obj instanceof Integer) {
                    num1 = (Integer) num1Obj;
                } else if (num1Obj instanceof Double) {
                    num1 = ((Double) num1Obj).intValue();
                } else {
                    continue;
                }
                
                if (num2Obj instanceof Integer) {
                    num2 = (Integer) num2Obj;
                } else if (num2Obj instanceof Double) {
                    num2 = ((Double) num2Obj).intValue();
                } else {
                    continue;
                }
                
                if (userChoiceObj instanceof Boolean) {
                    userChoice = (Boolean) userChoiceObj;
                } else if (userChoiceObj instanceof Integer) {
                    userChoice = ((Integer) userChoiceObj) == 1; // 假设1表示true
                } else {
                    continue;
                }
                
                // 检查答案是否正确
                String key = num1 + "_" + num2;
                Boolean expectedChoice = correctAnswers.get(key);
                
                if (expectedChoice == null || !expectedChoice.equals(userChoice)) {
                    return false;
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