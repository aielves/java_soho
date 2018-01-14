package com.soho.shiro.oauth2.controller;

import com.soho.shiro.oauth2.service.OAuth2AuthzService;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class OAuth2AuthzController {

    @Autowired(required = false)
    private OAuth2AuthzService oAuth2Service;

    @RequestMapping(value = "/oauth2.0/authorize")
    public Object authorize(HttpServletRequest request, HttpServletResponse response) {
        try {
            return oAuth2Service.authorize(request, response);
        } catch (Exception e) {
            return new ResponseEntity(
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/oauth2.0/token", method = RequestMethod.POST)
    @ResponseBody
    public Object access_token(HttpServletRequest request, HttpServletResponse response) {
        try {
            return oAuth2Service.access_token(request, response);
        } catch (OAuthSystemException e) {
            return OAuthError.TokenResponse.INVALID_REQUEST;
        }
    }

    @RequestMapping(value = "/oauth2.0/userinfo", method = RequestMethod.POST)
    @ResponseBody
    public Object userinfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            return oAuth2Service.userinfo(request, response);
        } catch (Exception e) {
            return new ResponseEntity(
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/oauth2.0/logout", method = RequestMethod.POST)
    @ResponseBody
    public Object logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            return oAuth2Service.logout_token(request, response);
        } catch (OAuthSystemException e) {
            return new ResponseEntity(
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}