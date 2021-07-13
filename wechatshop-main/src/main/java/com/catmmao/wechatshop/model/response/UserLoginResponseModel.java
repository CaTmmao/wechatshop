package com.catmmao.wechatshop.model.response;

import com.catmmao.wechatshop.generated.User;

public class UserLoginResponseModel {
    private Boolean isLogin;
    private User user;

    private UserLoginResponseModel(boolean isLogin, User user) {
        this.isLogin = isLogin;
        this.user = user;
    }

    public static UserLoginResponseModel notLogin() {
        return new UserLoginResponseModel(false, null);
    }

    public static UserLoginResponseModel loggedIn(User user) {
        return new UserLoginResponseModel(true, user);
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
