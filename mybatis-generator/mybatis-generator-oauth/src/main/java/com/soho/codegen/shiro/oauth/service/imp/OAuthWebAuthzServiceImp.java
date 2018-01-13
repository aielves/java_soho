package com.soho.codegen.shiro.oauth.service.imp;

import com.soho.codegen.shiro.oauth.service.OAuthWebAuthzService;
import com.soho.ex.BizErrorEx;
import com.soho.shiro.oauth2.aconst.OAuth2Client;
import com.soho.shiro.oauth2.aconst.OAuthBizErrorCode;
import com.soho.shiro.oauth2.server.OAuthClientService;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@Service
public class OAuthWebAuthzServiceImp implements OAuthWebAuthzService {

    @Autowired(required = false)
    private OAuthClientService oAuthClientService;

    public boolean oauthAutoLogin(Subject subject, HttpServletRequest request) throws BizErrorEx {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BizErrorEx(OAuthBizErrorCode.OAUTH_LOGIN_ERROR, "用户名/密码不能为空");
        }
        try {
            subject.login(new UsernamePasswordToken(username, password));
            return true;
        } catch (AuthenticationException e) {
            throw new BizErrorEx(OAuthBizErrorCode.OAUTH_LOGIN_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new BizErrorEx(OAuthBizErrorCode.OAUTH_LOGIN_ERROR, "登录系统繁忙,请稍后再试");
        }
    }

    public Object getAuthorizeCode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            // 构建OAuth 授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(httpServletRequest);
            // 检查传入的客户端id是否正确
            if (!oAuthClientService.validClientId(oauthRequest.getClientId())) {
                OAuthResponse response = OAuthASResponse
                        .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .setErrorDescription("client_id parameter error")
                        .buildJSONMessage();
                return new ResponseEntity(
                        response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }
            Subject subject = SecurityUtils.getSubject();
            //如果用户没有登录，跳转到登陆页面
            if (!subject.isAuthenticated()) {
                try {
                    oauthAutoLogin(subject, httpServletRequest);
                } catch (BizErrorEx ex) {
                    // WEB方式登录失败时跳转到登陆页面
                    ModelAndView view = new ModelAndView("index");
                    OAuth2Client oAuth2Client = new OAuth2Client();
                    oAuth2Client.setClient_id(oauthRequest.getClientId());
                    oAuth2Client.setRedirect_uri(oauthRequest.getRedirectURI());
                    oAuth2Client.setResponse_type(oauthRequest.getResponseType());
                    view.addObject("client", oAuth2Client);
                    view.addObject("errorMsg", ex.getMessage());
                    view.addObject("username", httpServletRequest.getParameter("username"));
                    view.addObject("password", httpServletRequest.getParameter("password"));
                    return view;
                }
            }
            String username = (String) subject.getPrincipal();
            //生成授权码
            String authorizationCode = null;
            //responseType目前仅支持CODE，另外还有TOKEN
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            if (ResponseType.CODE.toString().equals(responseType)) {
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                authorizationCode = oauthIssuerImpl.authorizationCode();
                oAuthClientService.addAuthCodeAndUsername(authorizationCode, username);
                oAuthClientService.addAuthCodeAndToken(authorizationCode, subject.getSession().getId().toString());
                oAuthClientService.addOAuthUserInfo(username, httpServletRequest.getParameter("password"));
            }
            //进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                    OAuthASResponse.authorizationResponse(httpServletRequest,
                            HttpServletResponse.SC_FOUND);
            //设置授权码
            builder.setCode(authorizationCode);
            //得到到客户端重定向地址
            String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
            //构建响应
            final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();
            //根据OAuthResponse返回ResponseEntity响应
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            //出错处理
            String redirectUri = e.getRedirectUri();
            if (OAuthUtils.isEmpty(redirectUri)) {
                //告诉客户端没有传入redirectUri直接报错
                return new ResponseEntity(
                        "The client's callback address was not found !", HttpStatus.NOT_FOUND);
            }
            //返回错误消息（如?error=）
            final OAuthResponse response =
                    OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                            .error(e).location(redirectUri).buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
        }
    }

    public Object getAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
        try {
            //构建OAuth请求
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(httpServletRequest);
            //检查提交的客户端id是否正确
            if (!oAuthClientService.validClientId(oauthRequest.getClientId())) {
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                .setErrorDescription("client_id parameter error")
                                .buildJSONMessage();
                return new ResponseEntity(
                        response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }
            // 检查客户端安全KEY是否正确
            if (!oAuthClientService.validClientSecret(oauthRequest.getClientId(), oauthRequest.getClientSecret())) {
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                                .setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
                                .setErrorDescription("client_secret parameter error")
                                .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }
            // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
            String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())) {
                if (!oAuthClientService.validAuthCode(authCode)) {
                    OAuthResponse response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
                            .setErrorDescription("authorization code error")
                            .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
                if (!oAuthClientService.validTokenByAuthCode(authCode)) {
                    OAuthResponse response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
                            .setErrorDescription("authorization code timeout")
                            .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
            }
            // 生成Access Token
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            final String accessToken = oauthIssuerImpl.accessToken();
            // final String accessToken = oAuthClientService.getTokenByAuthCode(authCode);
            String username = oAuthClientService.getUsernameByAuthCode(authCode);
            oAuthClientService.addTokenAndUsername(accessToken, username);
            //根据OAuthResponse生成ResponseEntity
            OAuthResponse response = OAuthASResponse
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setAccessToken(accessToken)
                    .setExpiresIn(String.valueOf(oAuthClientService.getExpire()))
                    .buildJSONMessage();
            return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            //构建错误响应
            OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e)
                    .buildJSONMessage();
            return new ResponseEntity(res.getBody(), HttpStatus.valueOf(res.getResponseStatus()));
        }
    }

    public Object getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            //构建OAuth资源请求
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(httpServletRequest, ParameterStyle.QUERY);
            //获取Access Token
            String accessToken = oauthRequest.getAccessToken();
            //验证Access Token
            String username = oAuthClientService.getUsernameByToken(accessToken);
            if (username == null) {
                // 如果不存在/过期了，返回未验证错误，需重新验证
                OAuthResponse oauthResponse = OAuthRSResponse
                        .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .setRealm("oauth-server")
                        .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
                        .buildHeaderMessage();
                HttpHeaders headers = new HttpHeaders();
                headers.add(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
                return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity(username, HttpStatus.OK);
            }
        } catch (OAuthProblemException e) {
            //检查是否设置了错误码
            String errorCode = e.getError();
            if (OAuthUtils.isEmpty(errorCode)) {
                OAuthResponse oauthResponse = OAuthRSResponse
                        .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .setRealm("oauth-server")
                        .buildHeaderMessage();
                HttpHeaders headers = new HttpHeaders();
                headers.add(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
                return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
            }
            OAuthResponse oauthResponse = OAuthRSResponse
                    .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                    .setRealm("oauth-server")
                    .setError(e.getError())
                    .setErrorDescription(e.getDescription())
                    .setErrorUri(e.getUri())
                    .buildHeaderMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.add(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

}
