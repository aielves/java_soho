package com.soho.shiro.filter;

import com.soho.shiro.filter.handler.OnAccessDeniedHandler;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 1.自定义角色鉴权过滤器(满足其中一个角色则认证通过) 2.扩展异步请求认证提示功能;
 *
 * @author shadow
 */
public class SimpleRoleAuthorizationFilter extends AuthorizationFilter {

    private OnAccessDeniedHandler onAccessDeniedHandler;

    private String redirect_uri;

    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Subject subject = getSubject(request, response);

        if (subject.getPrincipal() == null) {
            if (onAccessDeniedHandler != null && onAccessDeniedHandler.isRetJson(httpRequest, httpResponse)) {
                onAccessDeniedHandler.formAuthenOnAccessDenied(httpRequest, httpResponse);
            } else {
                saveRequestAndRedirectToLogin(request, response);
            }
        } else {
            if (onAccessDeniedHandler != null && onAccessDeniedHandler.isRetJson(httpRequest, httpResponse)) {
                onAccessDeniedHandler.roleAuthenOnAccessDenied(httpRequest, httpResponse);
            } else {
                String unauthorizedUrl = getUnauthorizedUrl();
                if (subject.isAuthenticated()) {
                    if (StringUtils.hasText(unauthorizedUrl)) {
                        WebUtils.issueRedirect(request, response, unauthorizedUrl);
                    } else {
                        WebUtils.toHttp(response).sendError(401);
                    }
                } else {
                    if (StringUtils.hasText(redirect_uri)) {
                        WebUtils.issueRedirect(request, response, redirect_uri);
                    } else {
                        WebUtils.toHttp(response).sendError(404);
                    }
                }
            }
        }
        return false;
    }

    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws IOException {

        Subject subject = getSubject(request, response);
        String[] rolesArray = (String[]) mappedValue;

        if (rolesArray == null || rolesArray.length == 0) {
            // no roles specified, so nothing to check - allow access.
            return true;
        }

        Set<String> roles = CollectionUtils.asSet(rolesArray);
        for (String role : roles) {
            if (subject.hasRole(role)) {
                return true;
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

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }
}