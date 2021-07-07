package com.catmmao.wechatshop.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class VerificationCodeCheckService {
    private Map<String, String> telToCorrectVerificationCode = new ConcurrentHashMap<>();

    public void add(String tel, String correctVerificationCode) {
        telToCorrectVerificationCode.put(tel, correctVerificationCode);
    }

    public String getCorrectVerificationCode(String tel) {
        return telToCorrectVerificationCode.get(tel);
    }
}
