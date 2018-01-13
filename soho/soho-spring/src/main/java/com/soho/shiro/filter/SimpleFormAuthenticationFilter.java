package com.soho.shiro.filter;

import com.soho.shiro.filter.handler.OnAccessDeniedHandler;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录过滤器扩展(支持异步请求异常提示)
 *
 * @author shadow
 */
public class SimpleFormAuthenticationFilter extends FormAuthenticationFilter {

    private OnAccessDeniedHandler onAccessDeniedHandler;

    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                return executeLogin(request, response);
            }
            return true;
        } else {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            if (onAccessDeniedHandler != null && onAccessDeniedHandler.isRetJson(httpRequest, httpResponse)) {
                onAccessDeniedHandler.formAuthenOnAccessDenied(httpRequest, httpResponse);
            } else {
                saveRequestAndRedirectToLogin(request, response);
            }
        }
        return false;
    }

    public OnAccessDeniedHandler getOnAccessDeniedHandler() {
        return onAccessDeniedHandler;
    }

    public void setOnAccessDeniedHandler(OnAccessDeniedHandler onAccessDeniedHandler) {
        this.onAccessDeniedHandler = onAccessDeniedHandler;
    }
}
