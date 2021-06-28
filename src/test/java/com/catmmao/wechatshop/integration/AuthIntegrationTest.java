package com.catmmao.wechatshop.integration;

import com.catmmao.wechatshop.WechatshopApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// AuthController 相关的集成测试
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechatshopApplication.class, // 启动应用的类
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 指定应用端口
public class AuthIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void sendCodeSucceed() {
        ResponseEntity<String> response = restTemplate.postForEntity(getUrl("/code"), CORRECT_ALL_PARAM, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void sendCodeFailIfDoNotHasParam() {
        ResponseEntity<String> response = restTemplate.postForEntity(getUrl("/code"), NULL_PARAM, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void needAuthenticatedControllerReturn401IfUserNonLoggedIn() {
        ResponseEntity<String> response = restTemplate.postForEntity(getUrl("/any"), null, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * 登录及登出集成测试
     * 对 controller 进行测试时记得手动带上 cookie,因为它不会和浏览器一样自己带上 cookie
     */
    @Test
    public void loginLogout() {
        /*
            1.查看登录状态: 发送 "/api/session" get 请求,返回未登录状态
            2.获取手机号验证码: 发送 "/api/code" post 请求,参数是手机号
            3.登录: 发送 "/api/session" post 请求,参数是手机号和手机验证码,登录后获取 cookie
            4.查看登录状态: 发送 "/api/session" get 请求,在 header 添加 cookie,返回已登录状态
            5.登出: 发送 "/api/session" delete 请求,在 header 添加 cookie
            6.查看登录状态: 发送 "/api/session" get 请求,在 header 添加 cookie,返回未登录状态
        */

        //1&2&3&4
        afterLoginReturnSessionIdAndUserInfo();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionId);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);

        //5.登出: 发送 "/api/session" delete 请求,在 header 添加 cookie
        ResponseEntity<String> response5 =
            restTemplate.exchange(getUrl("/session"), HttpMethod.DELETE, requestEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, response5.getStatusCode());

        //6.查看登录状态: 发送 "/api/session" get 请求,在 header 添加 cookie,返回未登录状态
        checkLoginStatus(requestEntity, false);
    }
}