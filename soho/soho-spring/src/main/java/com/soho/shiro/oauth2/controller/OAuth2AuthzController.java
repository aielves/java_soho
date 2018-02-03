package com.soho.shiro.oauth2.controller;

import com.soho.shiro.oauth2.service.OAuth2AuthzService;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class OAuth2AuthzController {

    public Object authorize(HttpServletRequest request, HttpServletResponse response) {
        try {
            return getOAuth2AuthzService().authorize(request, response);
        } catch (Exception e) {
            return new ResponseEntity(
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public Object access_token(HttpServletRequest request, HttpServletResponse response) {
        try {
            return getOAuth2AuthzService().access_token(request, response);
        } catch (OAuthSystemException e) {
            return OAuthError.TokenResponse.INVALID_REQUEST;
        }
    }

    public Object userinfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            return getOAuth2AuthzService().userinfo(request, response);
        } catch (Exception e) {
            return new ResponseEntity(
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public Object logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            return getOAuth2AuthzService().logout_token(request, response);
        } catch (OAuthSystemException e) {
            return new ResponseEntity(
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public Object refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            return getOAuth2AuthzService().refresh_token(request, response);
        } catch (OAuthSystemException e) {
            return new ResponseEntity(
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    protected abstract OAuth2AuthzService getOAuth2AuthzService();

}