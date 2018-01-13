/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.soho.codegen.shiro.service.imp;

import com.alibaba.fastjson.JSON;
import com.soho.codegen.shiro.aconst.BizErrorCode;
import com.soho.codegen.shiro.service.OAuthClientLoginService;
import com.soho.ex.BizErrorEx;
import com.soho.shiro.oauth2.aconst.OAuth2Client;
import com.soho.shiro.oauth2.client.OAuth2Token;
import com.soho.web.domain.Ret;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shadow on 2017/5/2.
 */
@Service
public class OAuthClientLoginServiceImp implements OAuthClientLoginService {

    @Autowired
    private OAuth2Client oAuth2Client;

    public Object oauthLogin(HttpServletRequest request, HttpServletResponse response, OAuth2Client oAuth2Client) throws Exception {
        validClienInfo(oAuth2Client);
        // 获取授权码authorize_code
        OAuthClientRequest authorizeCodeRequest = new OAuthClientRequest.TokenRequestBuilder(this.oAuth2Client.getAuthorize_code_uri())
                .setUsername(oAuth2Client.getUsername())
                .setPassword(oAuth2Client.getPassword())
                .setClientId(oAuth2Client.getClient_id())
                .setRedirectURI(oAuth2Client.getRedirect_uri())
                .setParameter("client_type", oAuth2Client.getClient_type())
                .setParameter("response_type", oAuth2Client.getResponse_type())
                .buildQueryMessage();
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthResourceResponse resourceResponse = oAuthClient.resource(authorizeCodeRequest, OAuth.HttpMethod.POST, OAuthResourceResponse.class);
        Ret<Map<String, String>> ret = JSON.parseObject(resourceResponse.getBody(), Ret.class);
        if (Ret.OK_STATUS.equals(ret.getCode())) {
            String authorize_code = ret.getData().get("authorize_code");
            try {
                OAuth2Token oAuth2Token = new OAuth2Token(authorize_code);
                SecurityUtils.getSubject().login(oAuth2Token);
                Map<String, String> map = new HashMap<String, String>();
                Session session = SecurityUtils.getSubject().getSession();
                map.put("access_token", session.getId().toString());
                return map;
            } catch (Exception e) {
                throw new BizErrorEx(BizErrorCode.OAUTH_LOGIN_ERROR, e.getMessage());
            }
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_LOGIN_ERROR, "OAUTH登录失败");
    }

    private void validClienInfo(OAuth2Client oAuth2Client) throws BizErrorEx {
        if (StringUtils.isEmpty(oAuth2Client.getClient_id())) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ID_ERROR, "client_id参数错误");
        }
        if (StringUtils.isEmpty(oAuth2Client.getClient_secret())) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_SECRET_ERROR, "client_secret参数错误");
        }
        if (StringUtils.isEmpty(oAuth2Client.getClient_type()) || (!"app".equals(oAuth2Client.getClient_type()) && !"web".equals(oAuth2Client.getClient_type()))) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_TYPE_ERROR, "client_type参数错误");
        }
        if (StringUtils.isEmpty(oAuth2Client.getRedirect_uri())) {
            throw new BizErrorEx(BizErrorCode.OAUTH_REDIRECT_URI_ERROR, "redirect_uri参数错误");
        }
        if (StringUtils.isEmpty(oAuth2Client.getUsername())) {
            throw new BizErrorEx(BizErrorCode.OAUTH_USERNAME_ERROR, "username参数错误");
        }
        if (StringUtils.isEmpty(oAuth2Client.getPassword())) {
            throw new BizErrorEx(BizErrorCode.OAUTH_PASSWORD_ERROR, "password参数错误");
        }
        if (StringUtils.isEmpty(oAuth2Client.getResponse_type()) && !"code".equals(oAuth2Client.getResponse_type())) {
            throw new BizErrorEx(BizErrorCode.OAUTH_RESPONSE_TYPE_ERROR, "response_type参数错误");
        }
    }


}
