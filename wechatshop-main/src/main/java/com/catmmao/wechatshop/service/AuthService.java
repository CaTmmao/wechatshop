package com.catmmao.wechatshop.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final SmsCodeService smsCodeService;
    private final VerificationCodeCheckService verificationCodeCheckService;

    public AuthService(UserService userService, SmsCodeService smsCodeService,
                       VerificationCodeCheckService verificationCodeCheckService) {
        this.userService = userService;
        this.smsCodeService = smsCodeService;
        this.verificationCodeCheckService = verificationCodeCheckService;
    }

    public void sendVerificationCodeToUserPhone(String tel) {
        userService.createUserIfNotExist(tel);
        String correctVerificationCode = smsCodeService.sendSmsCode(tel);
        verificationCodeCheckService.add(tel, correctVerificationCode);
    }
}
