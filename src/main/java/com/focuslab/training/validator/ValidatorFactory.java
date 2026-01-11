package com.focuslab.training.validator;

import com.focuslab.common.entity.TrainingType;
import com.focuslab.common.entity.TrainingContent;
import com.focuslab.common.repository.TrainingContentRepository;
import com.focuslab.training.validator.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 验证器工厂类
 * 根据训练类型返回相应的验证器
 */
@Component
public class ValidatorFactory {
    
    @Autowired
    private TrainingContentRepository trainingContentRepository;
    
    // 验证器映射
    private final Map<TrainingType, TrainingValidator> validators = new HashMap<>();
    
    // 初始化验证器映射
    public ValidatorFactory(ArrowTrackingValidator arrowTrackingValidator,
                           NumberRepetitionValidator numberRepetitionValidator,
                           AnimalNumberMatchValidator animalNumberMatchValidator,
                           HiddenObjectValidator hiddenObjectValidator,
                           NumberCompareValidator numberCompareValidator,
                           SchultzeTableValidator schultzeTableValidator,
                           SymbolMatrixValidator symbolMatrixValidator,
                           NumberMigrationValidator numberMigrationValidator) {
        validators.put(TrainingType.ARROW_TRACKING, arrowTrackingValidator);
        validators.put(TrainingType.NUMBER_REPETITION, numberRepetitionValidator);
        validators.put(TrainingType.ANIMAL_NUMBER_MATCH, animalNumberMatchValidator);
        validators.put(TrainingType.HIDDEN_OBJECT, hiddenObjectValidator);
        validators.put(TrainingType.NUMBER_COMPARE, numberCompareValidator);
        validators.put(TrainingType.SCHULTZ_TABLE, schultzeTableValidator);
        validators.put(TrainingType.SYMBOL_MATRIX, symbolMatrixValidator);
        validators.put(TrainingType.NUMBER_MIGRATION, numberMigrationValidator);
    }
    
    /**
     * 根据训练内容ID获取对应的验证器
     * @param contentId 训练内容ID
     * @return 对应的验证器
     */
    public TrainingValidator getValidator(Long contentId) {
        Optional<TrainingContent> content = trainingContentRepository.findById(contentId);
        if (content.isPresent()) {
            return validators.get(content.get().getType());
        }
        return null;
    }
    
    /**
     * 根据训练类型获取对应的验证器
     * @param type 训练类型
     * @return 对应的验证器
     */
    public TrainingValidator getValidator(TrainingType type) {
        return validators.get(type);
    }
    
    /**
     * 验证用户提交的答案
     * @param contentId 训练内容ID
     * @param userAnswers 用户提交的答案
     * @return 验证结果
     */
    public boolean validate(Long contentId, List<Object> userAnswers) {
        Optional<TrainingContent> content = trainingContentRepository.findById(contentId);
        if (content.isPresent()) {
            TrainingValidator validator = validators.get(content.get().getType());
            if (validator != null) {
                return validator.validate(userAnswers, content.get().getConfig());
            }
        }
        return false;
    }
}