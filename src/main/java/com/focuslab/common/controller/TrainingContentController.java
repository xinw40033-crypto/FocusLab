package com.focuslab.common.controller;

import com.focuslab.common.dto.Result;
import com.focuslab.common.dto.TrainingContentDto;
import com.focuslab.common.entity.TrainingContent;
import com.focuslab.common.entity.TrainingType;
import com.focuslab.common.service.TrainingContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training")
public class TrainingContentController {

    @Autowired
    private TrainingContentService trainingContentService;

    @GetMapping("/{id}")
    public ResponseEntity<Result<TrainingContentDto>> getTrainingContent(@PathVariable Long id) {
        try {
            TrainingContentDto content = trainingContentService.findById(id);
            if (content != null) {
                return ResponseEntity.ok(Result.success("获取训练内容成功", content));
            } else {
                return ResponseEntity.ok(Result.failure("未找到训练内容"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Result.failure("获取训练内容失败: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Result<List<TrainingContentDto>>> getAllTrainingContent(
            @RequestParam(required = false) TrainingType type,
            @RequestParam(required = false) Integer difficulty) {
        try {
            List<TrainingContentDto> contents;
            if (type != null && difficulty != null) {
                contents = trainingContentService.findByTypeAndDifficulty(type, difficulty);
            } else if (type != null) {
                contents = trainingContentService.findByType(type);
            } else if (difficulty != null) {
                contents = trainingContentService.findByDifficulty(difficulty);
            } else {
                contents = trainingContentService.findAll();
            }
            return ResponseEntity.ok(Result.success("获取训练内容列表成功", contents));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.failure("获取训练内容列表失败: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Result<TrainingContentDto>> createTrainingContent(@RequestBody TrainingContentDto contentDto) {
        try {
            TrainingContentDto createdContent = trainingContentService.create(contentDto);
            return ResponseEntity.ok(Result.success("创建训练内容成功", createdContent));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.failure("创建训练内容失败: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<TrainingContentDto>> updateTrainingContent(
            @PathVariable Long id, @RequestBody TrainingContentDto contentDto) {
        try {
            TrainingContentDto updatedContent = trainingContentService.update(id, contentDto);
            return ResponseEntity.ok(Result.success("更新训练内容成功", updatedContent));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.failure("更新训练内容失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteTrainingContent(@PathVariable Long id) {
        try {
            trainingContentService.delete(id);
            return ResponseEntity.ok(Result.success("删除训练内容成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.failure("删除训练内容失败: " + e.getMessage()));
        }
    }
}