package com.catmmao.wechatshop.service;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Service;

@Service
public class ShiroRealm extends AuthorizingRealm {
    private final VerificationCodeCheckService verificationCodeCheckService;

    public ShiroRealm(VerificationCodeCheckService verificationCodeCheckService) {
        this.verificationCodeCheckService = verificationCodeCheckService;
        this.setCredentialsMatcher((token, info) -> {
            String tokenPwd = new String((char[]) token.getCredentials());
            return tokenPwd.equals(info.getCredentials());
        });
    }

    /*
    权限控制
    */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /*
    角色匹配
    把用户提供的 token 与当前用户的信息进行比对，查看信息一样，是本人就可以登录
    */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String tel = (String) token.getPrincipal();
        String correctVerificationCode = verificationCodeCheckService.getCorrectVerificationCode(tel);
        return new SimpleAuthenticationInfo(tel, correctVerificationCode, getName());
    }
}
