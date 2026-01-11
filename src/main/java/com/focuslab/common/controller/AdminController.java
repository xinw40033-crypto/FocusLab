package com.focuslab.common.controller;

import com.focuslab.common.dto.ApiResponse;
import com.focuslab.common.entity.User;
import com.focuslab.common.entity.TrainingRecord;
import com.focuslab.common.repository.UserRepository;
import com.focuslab.common.repository.TrainingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingRecordRepository trainingRecordRepository;

    /**
     * 检查管理员权限
     */
    private boolean checkAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role);
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        
        if (!checkAdmin(session)) {
            return ApiResponse.failure("无权限访问");
        }

        Page<User> userPage = userRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        Map<String, Object> result = new HashMap<>();
        result.put("users", userPage.getContent());
        result.put("total", userPage.getTotalElements());
        result.put("page", page);
        result.put("size", size);

        return ApiResponse.success("获取成功", result);
    }

    /**
     * 禁用/启用用户
     */
    @PutMapping("/users/{userId}/status")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> params,
            HttpSession session) {
        
        if (!checkAdmin(session)) {
            return ApiResponse.failure("无权限访问");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.failure("用户不存在");
        }

        Integer status = params.get("status");
        user.setStatus(status);
        userRepository.save(user);

        return ApiResponse.success("更新成功", null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId, HttpSession session) {
        if (!checkAdmin(session)) {
            return ApiResponse.failure("无权限访问");
        }

        userRepository.deleteById(userId);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 获取所有训练记录（关联用户名）
     */
    @GetMapping("/records")
    public ApiResponse<Map<String, Object>> getRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        
        if (!checkAdmin(session)) {
            return ApiResponse.failure("无权限访问");
        }

        Page<TrainingRecord> recordPage = trainingRecordRepository.findAll(
                PageRequest.of(page, size, Sort.by("completedAt").descending())
        );

        // 关联用户名称
        List<Map<String, Object>> recordsWithUserName = recordPage.getContent().stream()
            .map(record -> {
                Map<String, Object> recordMap = new HashMap<>();
                recordMap.put("id", record.getId());
                recordMap.put("userId", record.getUserId());
                recordMap.put("trainingType", record.getTrainingType());
                recordMap.put("difficultyLevel", record.getDifficultyLevel());
                recordMap.put("score", record.getScore());
                recordMap.put("accuracyRate", record.getAccuracyRate());
                recordMap.put("timeSpent", record.getTimeSpent());
                recordMap.put("completedAt", record.getCompletedAt());
                
                // 查询用户名
                User user = userRepository.findById(record.getUserId()).orElse(null);
                recordMap.put("username", user != null ? user.getUsername() : "未知用户");
                
                return recordMap;
            })
            .collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", recordsWithUserName);
        result.put("total", recordPage.getTotalElements());
        result.put("page", page);
        result.put("size", size);

        return ApiResponse.success("获取成功", result);
    }

    /**
     * 获取系统统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(HttpSession session) {
        if (!checkAdmin(session)) {
            return ApiResponse.failure("无权限访问");
        }

        long totalUsers = userRepository.count();
        long totalRecords = trainingRecordRepository.count();
        
        List<User> recentUsers = userRepository.findAll(
                PageRequest.of(0, 5, Sort.by("createdAt").descending())
        ).getContent();

        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", totalUsers);
        result.put("totalRecords", totalRecords);
        result.put("recentUsers", recentUsers);

        return ApiResponse.success("获取成功", result);
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/users/{userId}/password")
    public ApiResponse<Void> resetPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> params,
            HttpSession session) {
        
        if (!checkAdmin(session)) {
            return ApiResponse.failure("无权限访问");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.failure("用户不存在");
        }

        String newPassword = params.get("password");
        user.setPassword(newPassword);
        userRepository.save(user);

        return ApiResponse.success("密码重置成功", null);
    }
}
