package com.catmmao.wechatshop.service;

import com.catmmao.wechatshop.model.TelAndCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TelVerificationServiceTest {
    private static TelAndCode correctTelFormat = new TelAndCode("13544444444", null);
    private static TelAndCode noTel = new TelAndCode(null, null);

    @Test
    public void verifySucceed() {
        // TelVerificationService 没有任何依赖，因此直接实例化就可以了
        Assertions.assertTrue(new TelVerificationService().verifyTel(correctTelFormat));
    }

    @Test
    public void verifyFailIfDoNotHasTel() {
        Assertions.assertFalse(new TelVerificationService().verifyTel(noTel));
    }

    @Test
    public void verifyFailIfDoNotHaseParam() {
        Assertions.assertFalse(new TelVerificationService().verifyTel(null));
    }
}