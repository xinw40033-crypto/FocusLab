package com.focuslab.training.validator;

import java.util.List;

/**
 * 训练验证器接口
 */
public interface TrainingValidator {
    /**
     * 验证用户提交的答案是否正确
     * @param userAnswers 用户提交的答案
     * @param config 题目配置信息（JSON格式）
     * @return 验证结果
     */
    boolean validate(List<Object> userAnswers, String config);
}