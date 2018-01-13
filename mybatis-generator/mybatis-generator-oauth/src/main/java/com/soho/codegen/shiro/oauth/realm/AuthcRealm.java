/**
 * Copyright ${license.git.copyrightYears} the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soho.codegen.shiro.oauth.realm;

/**
 * Created by  on 2017/4/27.
 */

import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.service.OauthUserService;
import com.soho.ex.BizErrorEx;
import com.soho.mybatis.sqlcode.condition.imp.SQLCnd;
import com.soho.zookeeper.security.imp.AESDcipher;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthcRealm extends AuthorizingRealm {

    @Autowired
    private OauthUserService oauthUserService;

    private CacheManager cacheManager;

    public AuthcRealm() {
        super();
        setCacheManager(cacheManager);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorInfo = new SimpleAuthorizationInfo();
        // String username = principals.getPrimaryPrincipal().toString();
        simpleAuthorInfo.addRole("user");
        return simpleAuthorInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        try {
            String password = new AESDcipher().encode(new String(token.getPassword()));
            OauthUser user = oauthUserService.findOneByCnd(new SQLCnd().eq("username", token.getUsername()).eq("password", password));
            if (user != null) {
                return new SimpleAuthenticationInfo(token.getUsername(), token.getCredentials(), getName());
            }
        } catch (BizErrorEx ex) {
            ex.printStackTrace();
        }
        throw new AuthenticationException("帐号/密码错误");
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

}