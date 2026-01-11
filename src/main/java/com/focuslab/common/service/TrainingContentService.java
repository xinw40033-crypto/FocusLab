package com.focuslab.common.service;

import com.focuslab.common.dto.TrainingContentDto;
import com.focuslab.common.entity.TrainingType;
import java.util.List;

public interface TrainingContentService {
    TrainingContentDto findById(Long id);
    List<TrainingContentDto> findAll();
    List<TrainingContentDto> findByType(TrainingType type);
    List<TrainingContentDto> findByDifficulty(Integer difficulty);
    List<TrainingContentDto> findByTypeAndDifficulty(TrainingType type, Integer difficulty);
    TrainingContentDto create(TrainingContentDto contentDto);
    TrainingContentDto update(Long id, TrainingContentDto contentDto);
    void delete(Long id);
}