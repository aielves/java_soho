package com.soho.shiro.oauth2.token;

import org.apache.shiro.authc.AuthenticationToken;

public class OAuth2WebToken implements AuthenticationToken {

    private String code;

    public OAuth2WebToken(String code) {
        this.code = code;
    }

    @Override
    public Object getPrincipal() {
        return this.code;
    }

    @Override
    public Object getCredentials() {
        return this.code;
    }

}
