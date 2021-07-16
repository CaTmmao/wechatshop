package com.catmmao.wechatshop.model.response;

import com.catmmao.wechatshop.generated.User;

public class UserLoginResponse {
    private Boolean isLogin;
    private User user;

    private UserLoginResponse(boolean isLogin, User user) {
        this.isLogin = isLogin;
        this.user = user;
    }

    public static UserLoginResponse notLogin() {
        return new UserLoginResponse(false, null);
    }

    public static UserLoginResponse loggedIn(User user) {
        return new UserLoginResponse(true, user);
    }

    public boolean isLogin() {
        return isLogin;
    }

    public User getUser() {
        return user;
    }

    public void setLogin(Boolean login) {
        isLogin = login;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
