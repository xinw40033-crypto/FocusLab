package com.focuslab.common.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.focuslab.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/captcha")
@CrossOrigin
public class CaptchaController {

    /**
     * 生成验证码
     */
    @GetMapping("/generate")
    public ApiResponse<Map<String, String>> generateCaptcha(HttpSession session) {
        // 创建线性验证码（宽200，高80，验证码长度4，干扰线数量10）
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 80, 4, 10);
        
        // 获取验证码文本
        String code = captcha.getCode();
        
        // 将验证码存入session，有效期5分钟
        session.setAttribute("captcha", code.toLowerCase()); // 转小写存储，便于不区分大小写验证
        session.setAttribute("captchaTime", System.currentTimeMillis());
        
        // 获取图片Base64编码
        String imageBase64 = captcha.getImageBase64();
        
        Map<String, String> result = new HashMap<>();
        result.put("image", "data:image/png;base64," + imageBase64);
        
        return ApiResponse.success("验证码生成成功", result);
    }
    
    /**
     * 验证验证码（用于测试）
     */
    @PostMapping("/verify")
    public ApiResponse<Void> verifyCaptcha(@RequestBody Map<String, String> params, HttpSession session) {
        String inputCode = params.get("code");
        
        if (inputCode == null || inputCode.trim().isEmpty()) {
            return ApiResponse.failure("验证码不能为空");
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
        if (!inputCode.toLowerCase().equals(sessionCode)) {
            return ApiResponse.failure("验证码错误");
        }
        
        // 验证成功后清除验证码（一次性使用）
        session.removeAttribute("captcha");
        session.removeAttribute("captchaTime");
        
        return ApiResponse.success("验证成功", null);
    }
}
