package com.catmmao.wechatshop.controller;

import com.catmmao.wechatshop.model.TelAndCode;
import com.catmmao.wechatshop.service.AuthService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 发送手机验证码
     * @param telAndCode 手机号
     */
    @PostMapping("/code")
    public void sendVerificationCodeToUserPhone(@RequestBody TelAndCode telAndCode) {
        authService.sendVerificationCodeToUserPhone(telAndCode.getTel());
    }

    /**
     * 登录
     * @param telAndCode 手机号和验证码
     *
     */
    @PostMapping("/session")
    public void login(@RequestBody TelAndCode telAndCode) {
        UsernamePasswordToken token = new UsernamePasswordToken(
            telAndCode.getTel(),
            telAndCode.getCode()
        );

        token.setRememberMe(true);

        SecurityUtils.getSubject().login(token);
    }
}
