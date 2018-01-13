package com.soho.codegen.shiro.oauth.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/client")
public class OAuthClientController {

    @RequestMapping(value = "/logout")
    @ResponseBody
    public Object logout() throws Exception {
        SecurityUtils.getSubject().logout();
        return new ModelAndView("redirect:/?v=" + System.currentTimeMillis());
    }

}