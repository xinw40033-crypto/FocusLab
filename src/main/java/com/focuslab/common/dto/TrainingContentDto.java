package com.focuslab.common.dto;

import com.focuslab.common.entity.TrainingType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class TrainingContentDto {

    private Long id;

    @NotNull(message = "训练类型不能为空")
    private TrainingType type;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "图片路径不能为空")
    private String imagePath;

    @NotBlank(message = "配置信息不能为空")
    private String config; // JSON格式

    @NotNull(message = "难度等级不能为空")
    @Min(value = 1, message = "难度等级最小值为1")
    @Max(value = 5, message = "难度等级最大值为5")
    private Integer difficulty;

    // Constructors
    public TrainingContentDto() {}

    public TrainingContentDto(TrainingType type, String title, String imagePath, String config, Integer difficulty) {
        this.type = type;
        this.title = title;
        this.imagePath = imagePath;
        this.config = config;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TrainingType getType() {
        return type;
    }

    public void setType(TrainingType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
}