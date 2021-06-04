package com.catmmao.wechatshop.config;

import java.util.HashMap;
import java.util.Map;

import com.catmmao.wechatshop.service.ShiroRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {
    private final ShiroRealm shiroRealm;

    public ShiroConfig(ShiroRealm shiroRealm) {
        this.shiroRealm = shiroRealm;
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(shiroRealm);

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

        shiroFilterFactoryBean.setFilterChainDefinitionMap(pattern);
        return shiroFilterFactoryBean;
    }

}
