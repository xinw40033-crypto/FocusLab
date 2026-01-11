package com.focuslab.common.controller;

import com.focuslab.common.dto.ApiResponse;
import com.focuslab.common.entity.User;
import com.focuslab.common.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> params, HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");
        String captcha = params.get("captcha");

        if (username == null || password == null) {
            return ApiResponse.failure("用户名和密码不能为空");
        }
        
        // 验证验证码
        if (captcha == null || captcha.trim().isEmpty()) {
            return ApiResponse.failure("请输入验证码");
        }
        
        String sessionCode = (String) session.getAttribute("captcha");
        Long captchaTime = (Long) session.getAttribute("captchaTime");
        
        if (sessionCode == null) {
            return ApiResponse.failure("验证码已失效，请重新获取");
        }
        
        // 检查验证码是否过期（5分钟）
        if (captchaTime != null && System.currentTimeMillis() - captchaTime > 5 * 60 * 1000) {
            session.removeAttribute("captcha");
            session.removeAttribute("captchaTime");
            return ApiResponse.failure("验证码已过期，请重新获取");
        }
        
        // 验证码验证（不区分大小写）
        if (!captcha.toLowerCase().equals(sessionCode)) {
            return ApiResponse.failure("验证码错误");
        }
        
        // 验证成功后清除验证码（一次性使用）
        session.removeAttribute("captcha");
        session.removeAttribute("captchaTime");

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ApiResponse.failure("用户不存在");
        }

        if (user.getStatus() == 0) {
            return ApiResponse.failure("账号已被禁用");
        }

        if (!user.getPassword().equals(password)) {
            return ApiResponse.failure("密码错误");
        }

        // 登录成功，存储到session
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("email", user.getEmail());

        return ApiResponse.success("登录成功", result);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, String> params, HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");
        String email = params.get("email");
        String captcha = params.get("captcha");
        String gender = params.get("gender");
        String ageStr = params.get("age");

        if (username == null || password == null) {
            return ApiResponse.failure("用户名和密码不能为空");
        }
        
        // 验证性别
        if (gender == null || gender.trim().isEmpty()) {
            return ApiResponse.failure("请选择性别");
        }
        
        if (!"男".equals(gender) && !"女".equals(gender) && !"其他".equals(gender)) {
            return ApiResponse.failure("性别选项不合法");
        }
        
        // 验证年龄
        if (ageStr == null || ageStr.trim().isEmpty()) {
            return ApiResponse.failure("请输入年龄");
        }
        
        Integer age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            return ApiResponse.failure("年龄必顾为数字");
        }
        
        if (age < 1 || age > 120) {
            return ApiResponse.failure("请输入1-120之间的年龄");
        }
        
        // 验证验证码
        if (captcha == null || captcha.trim().isEmpty()) {
            return ApiResponse.failure("请输入验证码");
        }
        
        String sessionCode = (String) session.getAttribute("captcha");
        Long captchaTime = (Long) session.getAttribute("captchaTime");
        
        if (sessionCode == null) {
            return ApiResponse.failure("验证码已失效，请重新获取");
        }
        
        // 检查验证码是否过期（5分钟）
        if (captchaTime != null && System.currentTimeMillis() - captchaTime > 5 * 60 * 1000) {
            session.removeAttribute("captcha");
            session.removeAttribute("captchaTime");
            return ApiResponse.failure("验证码已过期，请重新获取");
        }
        
        // 验证码验证（不区分大小写）
        if (!captcha.toLowerCase().equals(sessionCode)) {
            return ApiResponse.failure("验证码错误");
        }
        
        // 验证成功后清除验证码（一次性使用）
        session.removeAttribute("captcha");
        session.removeAttribute("captchaTime");

        // 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            return ApiResponse.failure("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setGender(gender);
        user.setAge(age);
        user.setRole("STUDENT");
        user.setStatus(1);
        user.setCreatedAt(java.time.LocalDateTime.now()); // 手动设置createdAt

        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());

        return ApiResponse.success("注册成功", result);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success("退出成功", null);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public ApiResponse<Map<String, Object>> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.failure("未登录");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.failure("用户不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("email", user.getEmail());

        return ApiResponse.success("获取成功", result);
    }
}
