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
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.service.OauthUserService;
import com.soho.ex.BizErrorEx;
import com.soho.mybatis.sqlcode.condition.imp.SQLCnd;
import com.soho.shiro.oauth2.aconst.OAuth2Client;
import com.soho.shiro.oauth2.client.OAuth2Token;
import com.soho.web.domain.Ret;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private OAuth2Client oAuth2Client;
    @Autowired
    private OauthUserService oauthUserService;

    private CacheManager cacheManager;

    public OAuth2Realm() {
        super();
        setCacheManager(cacheManager);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;//表示此Realm只支持OAuth2Token类型
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
        OAuth2Token oAuth2Token = (OAuth2Token) token;
        String code = oAuth2Token.getAuthCode();
        String username = getOAuthUsername(code);
        try {
            OauthUser user = oauthUserService.findOneByCnd(new SQLCnd().eq("username", username));
            if (user != null) {
                Session session = SecurityUtils.getSubject().getSession();
                session.setAttribute("session_user", user);
            } else {
                throw new AuthenticationException("没有找到用户数据:" + username);
            }
        } catch (BizErrorEx ex) {
            throw new AuthenticationException(ex.getMessage());
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(username, code, getName());
        return authenticationInfo;
    }

    private String getOAuthUsername(String code) {
        try {
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthClientRequest accessTokenRequest = OAuthClientRequest
                    .tokenLocation(oAuth2Client.getAccess_token_uri())
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(oAuth2Client.getClient_id())
                    .setClientSecret(oAuth2Client.getClient_secret())
                    .setCode(code)
                    .setRedirectURI(oAuth2Client.getRedirect_uri())
                    .buildQueryMessage();
            OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);
            String accessToken = oAuthResponse.getAccessToken();
            // Long expiresIn = oAuthResponse.getExpiresIn();
            OAuthClientRequest userInfoRequest = new OAuthBearerClientRequest(oAuth2Client.getUserinfo_uri())
                    .setAccessToken(accessToken).buildQueryMessage();
            OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            String username = resourceResponse.getBody();
            return username;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthenticationException(e);
        }
    }

    public OAuth2Client getoAuth2Client() {
        return oAuth2Client;
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
