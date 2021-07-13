package com.catmmao.wechatshop;

import com.catmmao.wechatshop.generated.User;

/*
存储用户具体信息

好处:
减少处理请求过程中获取用户信息的代码量
如每次需要获取用户具体信息时,都需要访问一次数据库
 Object tel = SecurityUtils.getSubject().getPrincipal();
 if (tel != null) {
     User user = userService.getUserByTel(tel.toString());
 }

使用步骤:
1.在请求拦截器 preHandle 方法中先判断用户是否登录
2.若用户已登录,从数据库中获取用户具体信息
3.调用该类的 setCurrentUser 方法,将信息存储在 ThreadLocal 中(每个线程内存储自己的用户信息)
4.服务器可在处理请求过程中的任意地方用该类的 getCurrentUser 方法从当前线程的 ThreadLocal 对象中获取用户信息
5.在拦截器 postHandle 阶段清除 ThreadLocal 中的信息,避免下次线程复用时出现串号情况
(串号:用户A可看到用户B的信息)
*/
public class UserContext {
    private static ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }
}
