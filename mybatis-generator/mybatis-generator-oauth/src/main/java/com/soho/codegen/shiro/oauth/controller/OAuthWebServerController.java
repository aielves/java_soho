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

import com.soho.codegen.shiro.oauth.service.OAuthWebAuthzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/oauth")
public class OAuthWebServerController {

    @Autowired
    private OAuthWebAuthzService oAuthWebAuthzService;

    @RequestMapping("/login")
    public Object getAuthorizeCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return oAuthWebAuthzService.getAuthorizeCode(request, response);
    }

    @RequestMapping("/access")
    public Object getAccessToken(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return oAuthWebAuthzService.getAccessToken(request, response);
    }

    @RequestMapping("/getUserInfo")
    public Object getUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return oAuthWebAuthzService.getUserInfo(request, response);
    }

}