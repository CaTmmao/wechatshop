package com.catmmao.wechatshop.service;

public interface SmsCodeService {
    /**
     * 向指定手机号发送手机验证码
     * @param tel 手机号
     * @return 验证码
     */
    String sendSmsCode(String tel);
}
