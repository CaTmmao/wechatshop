package com.catmmao.wechatshop.integration;

import java.util.List;

import com.catmmao.wechatshop.model.TelAndCode;
import com.catmmao.wechatshop.model.generated.User;
import com.catmmao.wechatshop.model.response.UserLoginResponseModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class AbstractIntegrationTest {
    protected static final TelAndCode CORRECT_TEL = new TelAndCode("13544444444", null);
    protected static final TelAndCode NULL_PARAM = new TelAndCode(null, null);
    protected static final TelAndCode CORRECT_ALL_PARAM = new TelAndCode("13544444444", "000000");

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected String getUrl(String api) {
        return "http://localhost:" + port + "/api/v1" + api;
    }

    /**
     * 获取 session id
     *
     * @param cookie 格式 -> "JSESSIONID=0c3a114e-3a59-4631-8ae0-2051ba3d375f; Path=/; HttpOnly; SameSite=lax"
     * @return sessionId 格式 -> "JSESSIONID=0c3a114e-3a59-4631-8ae0-2051ba3d375f"
     */
    protected String getSessionId(String cookie) {
        int endIndex = cookie.indexOf(";");
        return cookie.substring(0, endIndex);
    }

    /**
     * 查看登录状态
     *
     * @param entity  请求内容,包括 header 和 body
     * @param ifLogin 断言状态是否登录
     */
    protected UserLoginResponseModel checkLoginStatus(HttpEntity<?> entity, boolean ifLogin) {
        ResponseEntity<UserLoginResponseModel> response =
            restTemplate.exchange(getUrl("/session"), HttpMethod.GET, entity, UserLoginResponseModel.class);

        // 已登录
        if (ifLogin) {
            Assertions.assertTrue(response.getBody().isLogin());
        } else {
            Assertions.assertFalse(response.getBody().isLogin());
        }

        return response.getBody();
    }

    // 登录 && 返回登录后获取的 sessionId
    protected SessionIdAndUserInfo afterLoginReturnSessionIdAndUserInfo() {
        //1.查看登录状态: 发送 "/api/session" get 请求,返回未登录状态
        checkLoginStatus(null, false);

        //2.获取手机号验证码: 发送 "/api/code" post 请求,参数是手机号
        ResponseEntity<String> response2 = restTemplate.postForEntity(getUrl("/code"), CORRECT_TEL, String.class);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());


        //3.登录: 发送 "/api/session" post 请求,参数是手机号和手机验证码,登录后获取 cookie
        ResponseEntity<String> response3 =
            restTemplate.postForEntity(getUrl("/session"), CORRECT_ALL_PARAM, String.class);

        List<String> setCookieHeaderValues = response3.getHeaders().get(HttpHeaders.SET_COOKIE);
        Assertions.assertNotNull(setCookieHeaderValues);
        String cookie = setCookieHeaderValues
            .stream()
            .filter(i -> i.contains("JSESSIONID"))
            .findFirst()
            .get();

        String sessionId = getSessionId(cookie);

        //4.查看登录状态: 发送 "/api/session" get 请求,在 header 添加 cookie,返回已登录状态
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionId);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);
        UserLoginResponseModel userLoginResponseModel = checkLoginStatus(requestEntity, true);

        return new SessionIdAndUserInfo(sessionId, userLoginResponseModel.getUser());
    }

    public static class SessionIdAndUserInfo {
        String sessionId;
        User user;

        public SessionIdAndUserInfo(String sessionId, User user) {
            this.sessionId = sessionId;
            this.user = user;
        }
    }
}
