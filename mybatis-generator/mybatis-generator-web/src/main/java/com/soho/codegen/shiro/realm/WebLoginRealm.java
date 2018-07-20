package com.soho.codegen.shiro.realm;

import com.soho.codegen.domain.LoginLog;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.service.LoginLogService;
import com.soho.codegen.service.OauthUserService;
import com.soho.ex.BizErrorEx;
import com.soho.mybatis.sqlcode.condition.imp.SQLCnd;
import com.soho.shiro.utils.HttpUtils;
import com.soho.shiro.utils.SpringUtils;
import com.soho.utils.JAQUtils;
import com.soho.zookeeper.security.imp.AESDcipher;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class WebLoginRealm extends AuthorizingRealm {

    @Autowired
    private OauthUserService oauthUserService;
    @Autowired
    private LoginLogService loginLogService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (!JAQUtils.toStateByValid()) {
            throw new AuthenticationException("please carry out safety certification");
        }
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        if (StringUtils.isEmpty(token.getUsername()) || StringUtils.isEmpty(token.getPassword())) {
            throw new AuthenticationException("username/password is not allowed to be null");
        }
        String password = AESDcipher.encrypt(new String(token.getPassword()));
        try {
            OauthUser user = oauthUserService.findOneByCnd(new SQLCnd().eq("password", password).eq("username", token.getUsername()));
            if (user != null) {
                SecurityUtils.getSubject().getSession().setAttribute("session_user", user);
                JAQUtils.toStateByRemove();
                saveLoginLog(user.getId());
                return new SimpleAuthenticationInfo(token.getUsername(), token.getCredentials(), getName());
            }
        } catch (BizErrorEx ex) {
            ex.printStackTrace();
        }
        throw new AuthenticationException("username/password login error");
    }

    private void saveLoginLog(Long userId) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(userId);
        loginLog.setLogaddr(HttpUtils.getClientIP(SpringUtils.getRequest()));
        loginLog.setLogtime(System.currentTimeMillis());
        loginLog.setCtime(loginLog.getLogtime());
        try {
            loginLogService.insert(loginLog);
        } catch (BizErrorEx ex) {
            ex.printStackTrace();
        }
    }

}
