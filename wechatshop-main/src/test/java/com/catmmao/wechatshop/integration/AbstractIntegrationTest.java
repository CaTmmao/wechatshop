package com.catmmao.wechatshop.integration;

import java.util.List;

import com.catmmao.wechatshop.model.TelAndCode;
import com.catmmao.wechatshop.generated.User;
import com.catmmao.wechatshop.model.response.UserLoginResponseModel;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
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

    // cookie
    protected String sessionId;
    // 用户信息
    protected User userInfo;

    @LocalServerPort
    protected int port;
    @Autowired
    protected TestRestTemplate restTemplate;

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    @BeforeEach
    void initFlyway() {
        // 在每个测试开始前，执行一次flyway:clean flyway:migrate
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();
    }

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
     * @param ifLogin 断言状态是否登录
     */
    protected UserLoginResponseModel checkLoginStatus(boolean ifLogin) {
        ResponseEntity<UserLoginResponseModel> response =
            doHttpRequest(
                getUrl("/session"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<UserLoginResponseModel>() {
                });

        // 已登录
        if (ifLogin) {
            Assertions.assertTrue(response.getBody().isLogin());
        } else {
            Assertions.assertFalse(response.getBody().isLogin());
        }

        return response.getBody();
    }

    // 登录 && 返回登录后获取的 sessionId
    protected void afterLoginReturnSessionIdAndUserInfo() {
        //1.查看登录状态: 发送 "/api/session" get 请求,返回未登录状态
        checkLoginStatus(false);

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

        sessionId = getSessionId(cookie);

        //4.查看登录状态: 发送 "/api/session" get 请求,在 header 添加 cookie,返回已登录状态
        UserLoginResponseModel userLoginResponseModel = checkLoginStatus(true);
        userInfo = userLoginResponseModel.getUser();
    }

    /**
     * 发送请求
     * 当请求需要给 header 设置 cookie 时可以用该方法，避免重复代码
     *
     * @param url          请求地址
     * @param method       请求方法
     * @param requestBody  请求体
     * @param responseType 返回类型
     * @return 请求响应
     */
    protected <T, S> ResponseEntity<T> doHttpRequest(String url, HttpMethod method, S requestBody,
                                                     ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionId);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        HttpEntity<S> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(url, method, requestEntity, responseType);
    }
}
