package com.soho.shiro.filter;

import com.soho.shiro.oauth2.aconst.OAuth2Client;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * OAUTH2认证过滤器登录过滤器扩展(支持异步请求异常提示)
 *
 * @author shadow
 */
public class SimpleOAuth2AuthenticationFilter extends FormAuthenticationFilter {

    private OAuth2Client oAuth2Client;

    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = this.getSubject(request, response);
        if (!subject.isAuthenticated()) {
            StringBuffer uri = new StringBuffer();
            uri.append(oAuth2Client.getAuthorize_code_uri());
            uri.append("?client_id=").append(oAuth2Client.getClient_id());
            uri.append("&response_type=").append(oAuth2Client.getResponse_type());
            uri.append("&redirect_uri=").append(oAuth2Client.getRedirect_uri());
            WebUtils.issueRedirect(request, response, uri.toString());
            return false;
        } else {
            return this.executeLogin(request, response);
        }
    }

    public OAuth2Client getoAuth2Client() {
        return oAuth2Client;
    }

    public void setoAuth2Client(OAuth2Client oAuth2Client) {
        this.oAuth2Client = oAuth2Client;
    }
}
