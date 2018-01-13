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
package com.soho.codegen.shiro.service;

import com.soho.shiro.oauth2.server.OAuthClientService;
import com.soho.shiro.service.imp.AbstractFilterChainDefinitionsService;

import java.util.HashMap;
import java.util.Map;

public class SimpleFilterChainDefinitionsService extends AbstractFilterChainDefinitionsService {

    private Map<String, String> oauthClients;

    private OAuthClientService oAuthClientService;

    public Map<String, String> fetchOtherDefinitions() {
        for (Map.Entry<String, String> entry : oauthClients.entrySet()) {
            oAuthClientService.addOAuthClient(entry.getKey(), entry.getValue());
        }
        return new HashMap<String, String>();
    }

    public OAuthClientService getoAuthClientService() {
        return oAuthClientService;
    }

    public void setoAuthClientService(OAuthClientService oAuthClientService) {
        this.oAuthClientService = oAuthClientService;
    }

    public Map<String, String> getOauthClients() {
        return oauthClients;
    }

    public void setOauthClients(Map<String, String> oauthClients) {
        this.oauthClients = oauthClients;
    }
}