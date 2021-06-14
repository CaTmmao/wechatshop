package com.catmmao.wechatshop.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import com.catmmao.wechatshop.service.ShiroRealm;
import com.catmmao.wechatshop.service.UserLoginInterceptor;
import com.catmmao.wechatshop.service.UserService;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ShiroConfig implements WebMvcConfigurer {
    private final ShiroRealm shiroRealm;
    private final UserService userService;

    public ShiroConfig(ShiroRealm shiroRealm, UserService userService) {
        this.shiroRealm = shiroRealm;
        this.userService = userService;
    }

    // 请求拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInterceptor(userService));
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(shiroRealm);
        securityManager.setSessionManager(new DefaultWebSessionManager());

        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /*
        key 是接口地址; value 规定该接口是否需要登录才能访问

        value:
            - anon 不用登录就可以访问（anonymous 缩写）

        */
        Map<String, String> pattern = new HashMap<>();

        pattern.put("/api/code", "anon");
        pattern.put("/api/session", "anon");
        pattern.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(pattern);

        LinkedHashMap<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", new ShiroLoginFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        return shiroFilterFactoryBean;
    }
}
