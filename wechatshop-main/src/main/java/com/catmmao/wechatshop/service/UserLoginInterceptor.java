package com.catmmao.wechatshop.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.catmmao.wechatshop.UserContext;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Service
public class UserLoginInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public UserLoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object tel = SecurityUtils.getSubject().getPrincipal();

        // 已登录
        if (tel != null) {
            userService
                .getUserByTel(tel.toString())
                .ifPresent(UserContext::setCurrentUser);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        /*
        请求结束后意味着线程使用完毕,一定要清除 ThreadLocal 对象中的用户信息,因为线程是会复用的
        如线程 1 存储用户 A 的信息,如果没有清除信息,线程 1 下次处理其他用户的请求时,会发生串号情况
        */
        UserContext.setCurrentUser(null);
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
