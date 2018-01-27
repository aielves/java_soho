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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.shiro.aconst.BizErrorCode;
import com.soho.ex.BizErrorEx;
import com.soho.shiro.oauth2.aconst.OAuth2Client;
import com.soho.shiro.oauth2.token.OAuth2WebToken;
import com.soho.shiro.utils.HttpUtils;
import com.soho.shiro.utils.SessionUtils;
import com.soho.web.domain.Ret;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private OAuth2Client oAuth2Client;

    private CacheManager cacheManager;

    public OAuth2Realm() {
        super();
        setCacheManager(cacheManager);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2WebToken;//表示此Realm只支持OAuth2Token类型
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // String username = principals.getPrimaryPrincipal().toString();
        authorizationInfo.addRole("user");
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        try {
            OAuth2WebToken oAuth2WebToken = (OAuth2WebToken) token;
            String code = oAuth2WebToken.getPrincipal().toString();
            Map<String, String> accessToken = getAccessToken(code);
            OauthUser user = getUserInfo(accessToken.get("access_token"), accessToken.get("access_pbk"));
            SessionUtils.setUser(user);
            SessionUtils.setAttribute("tokeninfo", accessToken);
            SessionUtils.removeAttribute("state");
            return new SimpleAuthenticationInfo(user.getUid(), code, getName());
        } catch (BizErrorEx ex) {
            throw new AuthenticationException(ex.getMessage());
        }
    }

    // 获取授权令牌
    private Map<String, String> getAccessToken(String code) throws BizErrorEx {
        Map<String, String> map = new HashMap<>();
        map.put("client_id", oAuth2Client.getClient_id());
        map.put("client_secret", oAuth2Client.getClient_secret());
        map.put("grant_type", "authorization_code");
        map.put("code", code);
        map.put("redirect_uri", oAuth2Client.getRedirect_uri());
        String result = HttpUtils.sendPost(oAuth2Client.getAccess_token_uri(), map);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Ret.OK_STATUS.equals(jsonObject.getString("code"))) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (Ret.OK_STATUS.equals(data.getString("biz_code"))) {
                String access_token = data.getString("access_token");
                String access_pbk = data.getString("access_pbk");
                if (!StringUtils.isEmpty(access_token) && !StringUtils.isEmpty(access_pbk)) {
                    Map<String, String> retMap = new HashMap<>();
                    retMap.put("access_token", access_token);
                    retMap.put("access_pbk", access_pbk);
                    return retMap;
                }
            }
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_LOGIN_ERROR, "获取授权令牌失败");
    }

    // 获取用户信息
    private OauthUser getUserInfo(String access_token, String access_pbk) throws BizErrorEx {
        String result = HttpUtils.sendPost(oAuth2Client.getUserinfo_uri() + "?access_token=" + access_token + "&access_pbk=" + access_pbk, null);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Ret.OK_STATUS.equals(jsonObject.getString("code"))) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (Ret.OK_STATUS.equals(data.getString("biz_code"))) {
                String uid = data.getString("uid");
                if (!StringUtils.isEmpty(uid)) {
                    return JSON.parseObject(data.toJSONString(), OauthUser.class);
                }
            }
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_LOGIN_ERROR, "获取用户信息失败");
    }

    public void setoAuth2Client(OAuth2Client oAuth2Client) {
        this.oAuth2Client = oAuth2Client;
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
