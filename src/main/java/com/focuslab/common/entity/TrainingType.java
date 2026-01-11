package com.focuslab.common.entity;

public enum TrainingType {
    HIDDEN_OBJECT("寻宝游戏"),
    NUMBER_COMPARE("数字对比"),
    SCHULTZ_TABLE("舒尔特方格"),
    ARROW_TRACKING("箭头追踪"),
    NUMBER_REPETITION("数字重复查找"),
    ANIMAL_NUMBER_MATCH("动物数字匹配"),
    SYMBOL_MATRIX("符号矩阵"),
    NUMBER_MIGRATION("数字迁移");

    private final String description;

    TrainingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}