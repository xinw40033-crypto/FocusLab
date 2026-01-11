package com.focuslab.common.service.impl;

import com.focuslab.common.dto.TrainingContentDto;
import com.focuslab.common.entity.TrainingContent;
import com.focuslab.common.entity.TrainingType;
import com.focuslab.common.repository.TrainingContentRepository;
import com.focuslab.common.service.TrainingContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainingContentServiceImpl implements TrainingContentService {

    @Autowired
    private TrainingContentRepository trainingContentRepository;

    @Override
    public TrainingContentDto findById(Long id) {
        TrainingContent content = trainingContentRepository.findById(id).orElse(null);
        if (content != null) {
            return convertToDto(content);
        }
        return null;
    }

    @Override
    public List<TrainingContentDto> findAll() {
        return trainingContentRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingContentDto> findByType(TrainingType type) {
        return trainingContentRepository.findByType(type)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingContentDto> findByDifficulty(Integer difficulty) {
        return trainingContentRepository.findByDifficulty(difficulty)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingContentDto> findByTypeAndDifficulty(TrainingType type, Integer difficulty) {
        return trainingContentRepository.findByTypeAndDifficulty(type, difficulty)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TrainingContentDto create(TrainingContentDto contentDto) {
        TrainingContent content = convertToEntity(contentDto);
        TrainingContent savedContent = trainingContentRepository.save(content);
        return convertToDto(savedContent);
    }

    @Override
    public TrainingContentDto update(Long id, TrainingContentDto contentDto) {
        TrainingContent existingContent = trainingContentRepository.findById(id).orElse(null);
        if (existingContent != null) {
            // 更新实体的各个字段
            existingContent.setType(contentDto.getType());
            existingContent.setTitle(contentDto.getTitle());
            existingContent.setImagePath(contentDto.getImagePath());
            existingContent.setConfig(contentDto.getConfig());
            existingContent.setDifficulty(contentDto.getDifficulty());
            
            TrainingContent updatedContent = trainingContentRepository.save(existingContent);
            return convertToDto(updatedContent);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        trainingContentRepository.deleteById(id);
    }

    // 将实体转换为DTO
    private TrainingContentDto convertToDto(TrainingContent content) {
        TrainingContentDto dto = new TrainingContentDto();
        dto.setId(content.getId());
        dto.setType(content.getType());
        dto.setTitle(content.getTitle());
        dto.setImagePath(content.getImagePath());
        dto.setConfig(content.getConfig());
        dto.setDifficulty(content.getDifficulty());
        return dto;
    }

    // 将DTO转换为实体
    private TrainingContent convertToEntity(TrainingContentDto dto) {
        TrainingContent entity = new TrainingContent();
        entity.setId(dto.getId());
        entity.setType(dto.getType());
        entity.setTitle(dto.getTitle());
        entity.setImagePath(dto.getImagePath());
        entity.setConfig(dto.getConfig());
        entity.setDifficulty(dto.getDifficulty());
        return entity;
    }
}