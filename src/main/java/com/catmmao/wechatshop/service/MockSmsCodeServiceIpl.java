package com.catmmao.wechatshop.service;

import org.springframework.stereotype.Service;

@Service
public class MockSmsCodeServiceIpl implements SmsCodeService {
    @Override
    public String sendSmsCode(String tel) {
        return "000000";
    }
}
