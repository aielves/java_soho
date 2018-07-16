package com.soho.codegen.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.jaq.model.v20161123.AfsCheckRequest;
import com.aliyuncs.jaq.model.v20161123.AfsCheckResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.service.CodeGenService;
import com.soho.codegen.shiro.aconst.BizErrorCode;
import com.soho.ex.BizErrorEx;
import com.soho.utils.JAQUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("/user")
public class UserLoginController {

    @Autowired
    private CodeGenService codeGenService;

    @ResponseBody
    @RequestMapping(value = "/jaq")
    public Object jaq_valid(HttpServletRequest request) throws BizErrorEx {
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
        throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "JAQ验证失败");
    }

    @ResponseBody
    @RequestMapping(value = "/logout")
    public Object logout() {
        SecurityUtils.getSubject().logout();
        return new ModelAndView("redirect:/");
    }

    @ResponseBody
    @RequestMapping(value = "/login")
    public Object login(String username, String password) throws BizErrorEx {
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            SecurityUtils.getSubject().login(token);
            return new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AuthenticationException) {
                throw new BizErrorEx(BizErrorCode.BIZ_ERROR, e.getMessage());
            }
        }
        throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "login failed, please try again");
    }

    @ResponseBody
    @RequestMapping(value = "/signup")
    public Object signup(OauthUser user) throws BizErrorEx {
        return codeGenService.signup(user);
    }

}

