package com.catmmao.wechatshop.service;

import java.util.regex.Pattern;

import com.catmmao.wechatshop.model.TelAndCode;
import org.springframework.stereotype.Service;

@Service
public class TelVerificationService {
    static final Pattern TEL_PATTERN = Pattern.compile("1\\d{10}");

    public boolean verifyTel(TelAndCode telAndCode) {
        if (telAndCode == null) {
            return false;
        } else if (telAndCode.getTel() == null) {
            return false;
        } else {
            return TEL_PATTERN.matcher(telAndCode.getTel()).find();
        }
    }
}
