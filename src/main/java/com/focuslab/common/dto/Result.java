package com.focuslab.common.dto;

public class Result<T> {
    private String status;
    private String message;
    private T data;

    public Result() {}

    public Result(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public Result(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(String message) {
        return new Result<>("SUCCESS", message);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>("SUCCESS", message, data);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<>("FAILURE", message);
    }

    public static <T> Result<T> failure(String message, T data) {
        return new Result<>("FAILURE", message, data);
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}