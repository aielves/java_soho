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
package com.soho.codegen.shiro.oauth.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.jaq.model.v20161123.AfsCheckRequest;
import com.aliyuncs.jaq.model.v20161123.AfsCheckResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.soho.codegen.shiro.error.BizErrorCode;
import com.soho.ex.BizErrorEx;
import com.soho.shiro.oauth2.controller.OAuth2AuthzController;
import com.soho.shiro.oauth2.service.OAuth2AuthzService;
import com.soho.utils.JAQUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Controller
@RequestMapping
public class OAuth2AuthzV1Controller extends OAuth2AuthzController {

    @Autowired(required = false)
    private OAuth2AuthzService oAuth2AuthzService;

    @ResponseBody
    @RequestMapping(value = "/oauth2.0/v1/jaq")
    public Object jaq_valid(HttpServletRequest request, HttpServletResponse response) throws BizErrorEx {
        try {  // JAQ行为分析认证
            if (JAQUtils.toStateByValid()) {
                return new HashMap<>();
            }
            String csessionid = request.getParameter("csessionid");
            String sig = request.getParameter("sig");
            String token = request.getParameter("token");
            String scene = request.getParameter("scene");
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI6Lq7SoAzfMwE", "rXMwZH2RqgIRRLl9LipsoT432jAJYU");
            IAcsClient client = new DefaultAcsClient(profile);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Jaq", "jaq.aliyuncs.com");
            AfsCheckRequest afsCheckRequest = new AfsCheckRequest();
            afsCheckRequest.setPlatform(3);//必填参数，请求来源： 1：Android端； 2：iOS端； 3：PC端及其他
            afsCheckRequest.setSession(csessionid);// 必填参数，从前端获取，不可更改
            afsCheckRequest.setSig(sig);// 必填参数，从前端获取，不可更改
            afsCheckRequest.setToken(token);// 必填参数，从前端获取，不可更改
            afsCheckRequest.setScene(scene);// 必填参数，从前端获取，不可更改
            AfsCheckResponse afsCheckResponse = client.getAcsResponse(afsCheckRequest);
            if (afsCheckResponse.getErrorCode() == 0 && afsCheckResponse.getData() == true) {
                JAQUtils.toStateBySuccess();
                return new HashMap<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "JAQ验证失败");
    }

    @RequestMapping(value = "/oauth2.0/v1/authorize")
    public Object authorize(HttpServletRequest request, HttpServletResponse response) {
        return super.authorize(request, response);
    }

    @ResponseBody
    @RequestMapping(value = "/oauth2.0/v1/token", method = RequestMethod.POST)
    public Object access_token(HttpServletRequest request, HttpServletResponse response) {
        return super.access_token(request, response);
    }

    @ResponseBody
    @RequestMapping(value = "/oauth2.0/v1/userinfo", method = RequestMethod.POST)
    public Object userinfo(HttpServletRequest request, HttpServletResponse response) {
        return super.userinfo(request, response);
    }

    @ResponseBody
    @RequestMapping(value = "/oauth2.0/v1/logout", method = RequestMethod.POST)
    public Object logout(HttpServletRequest request, HttpServletResponse response) {
        return super.logout(request, response);
    }

    @Override
    protected OAuth2AuthzService getOAuth2AuthzService() {
        return oAuth2AuthzService;
    }

}