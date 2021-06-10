package com.catmmao.wechatshop.controller;

import javax.servlet.http.HttpServletResponse;

import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.model.TelAndCode;
import com.catmmao.wechatshop.model.generated.User;
import com.catmmao.wechatshop.model.response.UserLoginResponseModel;
import com.catmmao.wechatshop.service.AuthService;
import com.catmmao.wechatshop.service.TelVerificationService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final TelVerificationService telVerificationService;
    private final AuthService authService;

    public AuthController(TelVerificationService telVerificationService,
                          AuthService authService) {
        this.telVerificationService = telVerificationService;
        this.authService = authService;
    }

    /**
     * 发送手机验证码
     *
     * @param telAndCode 手机号
     */
    @PostMapping("/code")
    public void sendVerificationCodeToUserPhone(@RequestBody TelAndCode telAndCode,
                                                HttpServletResponse response) {
        if (telVerificationService.verifyTel(telAndCode)) {
            authService.sendVerificationCodeToUserPhone(telAndCode.getTel());
        } else {
            response.setStatus(response.SC_BAD_REQUEST);
        }
    }

    /**
     * 登录
     *
     * @param telAndCode 手机号和验证码
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

    /**
     * 获取用户登录状态
     *
     * @return 用户信息
     */
    @GetMapping("/session")
    public ResponseEntity<UserLoginResponseModel> getLoginStatus() {
        UserLoginResponseModel responseBody;

        if (UserContext.getCurrentUser() != null) {
            User user = UserContext.getCurrentUser();
            responseBody = UserLoginResponseModel.loggedIn(user);
        } else {
            responseBody = UserLoginResponseModel.notLogin();
        }

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping("/session")
    public void logout() {
        SecurityUtils.getSubject().logout();
    }
}
