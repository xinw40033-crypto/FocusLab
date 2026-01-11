package com.focuslab.training.validator.impl;

import com.focuslab.training.validator.TrainingValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 隐藏对象验证器（寻宝游戏）
 * 验证用户找到的对象位置是否正确
 */
@Component
public class HiddenObjectValidator implements TrainingValidator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean validate(List<Object> userAnswers, String config) {
        try {
            // 解析配置JSON
            JsonNode configNode = objectMapper.readTree(config);
            
            // 获取正确对象位置信息
            JsonNode objectsNode = configNode.get("objects");
            if (objectsNode == null || !objectsNode.isArray()) {
                return false;
            }
            
            // 构建正确答案映射（对象名称 -> [x, y]坐标）
            Map<String, int[]> correctLocations = new HashMap<>();
            for (JsonNode objNode : objectsNode) {
                JsonNode nameNode = objNode.get("name");
                JsonNode xNode = objNode.get("x");
                JsonNode yNode = objNode.get("y");
                
                if (nameNode != null && xNode != null && yNode != null) {
                    String name = nameNode.asText();
                    int x = xNode.asInt();
                    int y = yNode.asInt();
                    
                    correctLocations.put(name, new int[]{x, y});
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
                String objectName = (String) answerMap.get("objectName");
                Object coordinatesObj = answerMap.get("coordinates");
                
                if (objectName != null && coordinatesObj instanceof List) {
                    List<?> coords = (List<?>) coordinatesObj;
                    if (coords.size() >= 2) {
                        Object xObj = coords.get(0);
                        Object yObj = coords.get(1);
                        
                        int actualX, actualY;
                        if (xObj instanceof Integer) {
                            actualX = (Integer) xObj;
                        } else if (xObj instanceof Double) {
                            actualX = ((Double) xObj).intValue();
                        } else {
                            continue;
                        }
                        
                        if (yObj instanceof Integer) {
                            actualY = (Integer) yObj;
                        } else if (yObj instanceof Double) {
                            actualY = ((Double) yObj).intValue();
                        } else {
                            continue;
                        }
                        
                        // 检查该对象是否存在且坐标在容差范围内
                        int[] expectedCoords = correctLocations.get(objectName);
                        if (expectedCoords == null) {
                            return false; // 用户找到了不存在的对象
                        }
                        
                        // 检查坐标是否在容差范围内（±5px）
                        if (Math.abs(actualX - expectedCoords[0]) > 5 || Math.abs(actualY - expectedCoords[1]) > 5) {
                            return false;
                        }
                    }
                }
            }
            
            // 检查用户是否找到了所有对象
            return userAnswers.size() == correctLocations.size();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}