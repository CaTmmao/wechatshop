package com.catmmao.wechatshop.integration;

import java.util.List;

import com.catmmao.wechatshop.WechatshopApplication;
import com.catmmao.wechatshop.model.TelAndCode;
import com.catmmao.wechatshop.model.response.UserLoginResponseModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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
//@TestPropertySource(locations = "classpath:application.yml")
public class AuthIntegrationTest {
    private static final TelAndCode CORRECT_ALL_PARAM = new TelAndCode("13544444444", "000000");
    private static final TelAndCode CORRECT_TEL = new TelAndCode("13544444444", null);
    private static final TelAndCode NULL_PARAM = new TelAndCode(null, null);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String sendCodeApiUrl;

    private String sessionApiUrl;

    @BeforeEach
    void setup() {
        sendCodeApiUrl = getUrl("/api/code");
        sessionApiUrl = getUrl("/api/session");
    }

    private String getUrl(String api) {
        return "http://localhost:" + port + api;
    }

    @Test
    public void testSendCodeWithValidParamReturnOk() {
        ResponseEntity<String> response = restTemplate.postForEntity(sendCodeApiUrl, CORRECT_ALL_PARAM, String.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testSendCodeWithNoTelParamReturnBadRequest() {
        ResponseEntity<String> response = restTemplate.postForEntity(sendCodeApiUrl, NULL_PARAM, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testNeedAuthenticatedControllerWithUnLoginStatusReturn401() {
        ResponseEntity<String> response = restTemplate.postForEntity(getUrl("/api/any"), null, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * 登录及登出集成测试
     * 对 controller 进行测试时记得手动带上 cookie,因为它不会和浏览器一样自己带上 cookie
     */
    @Test
    public void loginLogoutTest() {
        /*
            1.查看登录状态: 发送 "/api/session" get 请求,返回未登录状态
            2.获取手机号验证码: 发送 "/api/code" post 请求,参数是手机号
            3.登录: 发送 "/api/session" post 请求,参数是手机号和手机验证码,登录后获取 cookie
            4.查看登录状态: 发送 "/api/session" get 请求,在 header 添加 cookie,返回已登录状态
            5.登出: 发送 "/api/session" delete 请求,在 header 添加 cookie
            6.查看登录状态: 发送 "/api/session" get 请求,在 header 添加 cookie,返回未登录状态
        */


        //1.查看登录状态: 发送 "/api/session" get 请求,返回未登录状态
        ResponseEntity<UserLoginResponseModel> response1 =
            restTemplate.getForEntity(sessionApiUrl, UserLoginResponseModel.class);
        Assertions.assertFalse(response1.getBody().isLogin());

        //2.获取手机号验证码: 发送 "/api/code" post 请求,参数是手机号
        ResponseEntity<String> response2 = restTemplate.postForEntity(sendCodeApiUrl, CORRECT_TEL, String.class);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());


        //3.登录: 发送 "/api/session" post 请求,参数是手机号和手机验证码,登录后获取 cookie
        ResponseEntity<String> response3 = restTemplate.postForEntity(sessionApiUrl, CORRECT_ALL_PARAM, String.class);

        List<String> setCookieHeaderValues = response3.getHeaders().get(HttpHeaders.SET_COOKIE);
        Assertions.assertNotNull(setCookieHeaderValues);
        String cookie = setCookieHeaderValues
            .stream()
            .filter(i -> i.contains("JSESSIONID"))
            .findFirst()
            .get();

        String sessionId = getSessionId(cookie);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionId);

        //4.查看登录状态: 发送 "/api/session" get 请求,在 header 添加 cookie,返回已登录状态
        HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<UserLoginResponseModel> response4 =
            restTemplate.exchange(sessionApiUrl, HttpMethod.GET, requestEntity, UserLoginResponseModel.class);
        Assertions.assertTrue(response4.getBody().isLogin());

        //5.登出: 发送 "/api/session" delete 请求,在 header 添加 cookie
        ResponseEntity<String> response5 =
            restTemplate.exchange(sessionApiUrl, HttpMethod.DELETE, requestEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, response5.getStatusCode());

        //6.查看登录状态: 发送 "/api/session" get 请求,在 header 添加 cookie,返回未登录状态
        ResponseEntity<UserLoginResponseModel> response6 =
            restTemplate.exchange(sessionApiUrl, HttpMethod.GET, requestEntity, UserLoginResponseModel.class);
        Assertions.assertFalse(response6.getBody().isLogin());
    }

    /**
     * 获取 session id
     *
     * @param cookie 格式 -> "JSESSIONID=0c3a114e-3a59-4631-8ae0-2051ba3d375f; Path=/; HttpOnly; SameSite=lax"
     * @return sessionId 格式 -> "JSESSIONID=0c3a114e-3a59-4631-8ae0-2051ba3d375f"
     */
    private String getSessionId(String cookie) {
        int endIndex = cookie.indexOf(";");
        return cookie.substring(0, endIndex);
    }
}