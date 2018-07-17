package com.soho.codegen.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigResponse;
import com.aliyuncs.jaq.model.v20161123.AfsCheckRequest;
import com.aliyuncs.jaq.model.v20161123.AfsCheckResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.service.CodeGenService;
import com.soho.codegen.shiro.aconst.BizErrorCode;
import com.soho.codegen.utils.GGKUtils;
import com.soho.ex.BizErrorEx;
import com.soho.shiro.utils.HttpUtils;
import com.soho.utils.JAQUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @RequestMapping(value = "/sendsms", method = RequestMethod.POST)
    public Object sendsms(String mobile) throws BizErrorEx {
        return codeGenService.sendsms(mobile);
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
    @RequestMapping(value = "/hasValid")
    public Object hashValid() throws BizErrorEx {
        if (!JAQUtils.toStateByValid()) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "请先进行安全认证");
        }
        return new HashMap<>();
    }

    @RequestMapping("/validGGK")
    public void validGGK(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String sessionid, String sig, String token, String scene) {
        AuthenticateSigRequest request = new AuthenticateSigRequest();
        request.setSessionId(sessionid);// 必填参数，从前端获取，不可更改，android和ios只变更这个参数即可，下面参数不变保留xxx
        request.setSig(sig);// 必填参数，从前端获取，不可更改
        request.setToken(token);// 必填参数，从前端获取，不可更改
        request.setScene(scene);// 必填参数，从前端获取，不可更改
        request.setAppKey("FFFF000000000179AE04");// 必填参数，后端填写
        request.setRemoteIp(HttpUtils.getClientIP(httpRequest));// 必填参数，后端填写
        try {
            AuthenticateSigResponse response = GGKUtils.iAcsClient.getAcsResponse(request);
            if (response.getCode() == 100) { // 验签通过
                JAQUtils.toStateBySuccess();
                httpResponse.sendRedirect("/static/signup.html");
                return;
            }
            httpResponse.sendRedirect("/static/ggk.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/signup")
    public Object signup(OauthUser user, String smscode) throws BizErrorEx {
        return codeGenService.signup(user, smscode);
    }

}

