package com.soho.shiro.oauth2.server.imp;

import com.alibaba.fastjson.JSON;
import com.soho.ex.BizErrorEx;
import com.soho.shiro.oauth2.aconst.OAuth2Client;
import com.soho.shiro.oauth2.aconst.OAuthBizErrorCode;
import com.soho.shiro.oauth2.server.OAuthAuthzService;
import com.soho.shiro.oauth2.server.OAuthClientService;
import com.soho.shiro.oauth2.server.OAuthUserService;
import com.soho.shiro.utils.HttpUtils;
import com.soho.web.domain.Ret;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuthAuthzServiceImp implements OAuthAuthzService {

    @Autowired(required = false)
    private OAuthClientService oAuthClientService;
    @Autowired(required = false)
    private OAuthUserService oAuthUserService;

    private boolean validClientType(HttpServletRequest httpServletRequest) throws Exception {
        boolean isAppClientType = false;
        if ("app".equals(httpServletRequest.getParameter("client_type"))) {
            isAppClientType = true;
        }
        return isAppClientType;
    }

    public boolean oauthAutoLogin(Subject subject, HttpServletRequest request) throws BizErrorEx {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BizErrorEx(OAuthBizErrorCode.OAUTH_LOGIN_ERROR, "username/password参数不能为空");
        }
        try {
            subject.login(new UsernamePasswordToken(username, password));
            return true;
        } catch (Exception e) {
            throw new BizErrorEx(OAuthBizErrorCode.OAUTH_LOGIN_ERROR, "登录失败:帐号/密码错误");
        }
    }

    public Object getAuthorizeCode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        boolean isAppClientType = validClientType(httpServletRequest);
        try {
            //构建OAuth 授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(httpServletRequest);
            //检查传入的客户端id是否正确
            if (!oAuthClientService.validClientId(oauthRequest.getClientId())) {
                if (isAppClientType) {
                    throw new BizErrorEx(OAuthBizErrorCode.OAUTH_CLIENT_ID_ERROR, "client_id参数错误");
                } else {
                    OAuthResponse response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                            .setErrorDescription("client_id参数错误")
                            .buildJSONMessage();
                    return new ResponseEntity(
                            response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
            }
            Subject subject = SecurityUtils.getSubject();
            //如果用户没有登录，跳转到登陆页面
            if (!subject.isAuthenticated()) {
                try {
                    oauthAutoLogin(subject, httpServletRequest);
                } catch (BizErrorEx ex) {
                    if (isAppClientType) {
                        throw ex;
                    } else {
                        // WEB方式登录失败时跳转到登陆页面
                        ModelAndView view = new ModelAndView("index");
                        OAuth2Client oAuth2Client = new OAuth2Client();
                        oAuth2Client.setClient_id(oauthRequest.getClientId());
                        oAuth2Client.setRedirect_uri(oauthRequest.getRedirectURI());
                        oAuth2Client.setResponse_type(oauthRequest.getResponseType());
                        oAuth2Client.setState(oauthRequest.getState());
                        view.addObject("client", oAuth2Client);
                        view.addObject("errorMsg", ex.getMessage());
                        view.addObject("username", httpServletRequest.getParameter("username"));
                        view.addObject("password", httpServletRequest.getParameter("password"));
                        return view;
                    }
                }
            }
            String username = (String) subject.getPrincipal();
            //生成授权码
            String authorizationCode = null;
            //responseType目前仅支持CODE，另外还有TOKEN
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            if (responseType.equals(ResponseType.CODE.toString())) {
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                authorizationCode = oauthIssuerImpl.authorizationCode();
                oAuthClientService.addAuthCodeAndUsername(authorizationCode, username);
                oAuthClientService.addAuthCodeAndToken(authorizationCode, subject.getSession().getId().toString());
                oAuthClientService.addOAuthUserInfo(username, httpServletRequest.getParameter("password"));
            }
            if (isAppClientType) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("authorize_code", authorizationCode);
                if (!StringUtils.isEmpty(oauthRequest.getState())) {
                    map.put("state", oauthRequest.getState());
                }
                Ret<Map<String, String>> ret = new Ret<>(Ret.OK_STATUS, Ret.OK_MESSAGE, map);
                HttpUtils.sendJsonMsg(httpServletResponse, JSON.toJSONString(ret));
                return new ModelAndView();
            } else {
                //进行OAuth响应构建
                OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                        OAuthASResponse.authorizationResponse(httpServletRequest,
                                HttpServletResponse.SC_FOUND);
                //设置授权码
                builder.setCode(authorizationCode);
                if (!StringUtils.isEmpty(oauthRequest.getState())) {
                    builder.setParam("state", oauthRequest.getState());
                }
                //得到到客户端重定向地址
                String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
                //构建响应
                final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();
                //根据OAuthResponse返回ResponseEntity响应
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(new URI(response.getLocationUri()));
                return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
            }
        } catch (OAuthProblemException e) {
            //出错处理
            String redirectUri = e.getRedirectUri();
            if (OAuthUtils.isEmpty(redirectUri)) {
                //告诉客户端没有传入redirectUri直接报错
                if (isAppClientType) {
                    throw new BizErrorEx(OAuthBizErrorCode.OAUTH_REDIRECT_URI_ERROR, "redirect_uri参数为空");
                } else {
                    return new ResponseEntity(
                            "OAuth callback url needs to be provided by client!!!", HttpStatus.NOT_FOUND);
                }
            }
            if (isAppClientType) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, e.getMessage());
            } else {
                //返回错误消息（如?error=）
                final OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                                .error(e).location(redirectUri).buildQueryMessage();
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(new URI(response.getLocationUri()));
                return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
            }
        }
    }

    public Object getAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
        boolean isAppClientType = validClientType(httpServletRequest);
        try {
            //构建OAuth请求
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(httpServletRequest);
            //检查提交的客户端id是否正确
            if (!oAuthClientService.validClientId(oauthRequest.getClientId())) {
                if (isAppClientType) {
                    throw new BizErrorEx(OAuthBizErrorCode.OAUTH_CLIENT_ID_ERROR, "client_id参数错误");
                } else {
                    OAuthResponse response =
                            OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                    .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                    .setErrorDescription("client_id参数错误")
                                    .buildJSONMessage();
                    return new ResponseEntity(
                            response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
            }
            // 检查客户端安全KEY是否正确
            if (!oAuthClientService.validClientSecret(oauthRequest.getClientId(), oauthRequest.getClientSecret())) {
                if (isAppClientType) {
                    throw new BizErrorEx(OAuthBizErrorCode.OAUTH_CLIENT_SECRET_ERROR, "client_secret参数错误");
                } else {
                    OAuthResponse response =
                            OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                                    .setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
                                    .setErrorDescription("client_secret参数错误")
                                    .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
            }
            // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
            String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())) {
                if (!oAuthClientService.validAuthCode(authCode)) {
                    if (isAppClientType) {
                        throw new BizErrorEx(OAuthBizErrorCode.OAUTH_AUTHORIZE_CODE_ERROR, "授权码错误");
                    } else {
                        OAuthResponse response = OAuthASResponse
                                .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_GRANT)
                                .setErrorDescription("授权码错误")
                                .buildJSONMessage();
                        return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                    }
                }
                if (!oAuthClientService.validTokenByAuthCode(authCode)) {
                    if (isAppClientType) {
                        throw new BizErrorEx(OAuthBizErrorCode.OAUTH_AUTHORIZE_CODE_TIMEOUT, "授权码失效");
                    } else {
                        OAuthResponse response = OAuthASResponse
                                .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_GRANT)
                                .setErrorDescription("授权码失效")
                                .buildJSONMessage();
                        return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                    }
                }
            }
            // 生成Access Token
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            final String accessToken = oauthIssuerImpl.accessToken();
            // final String accessToken = oAuthClientService.getTokenByAuthCode(authCode);
            String username = oAuthClientService.getUsernameByAuthCode(authCode);
            oAuthClientService.addTokenAndUsername(accessToken, username);
            if (username != null) {
                oAuthClientService.delAuthCodeAndUsername(authCode);
            }
            if (isAppClientType) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("access_token", accessToken);
                Ret ret = new Ret<Map<String, String>>(Ret.OK_STATUS, Ret.OK_MESSAGE, map);
                HttpUtils.sendJsonMsg(httpServletResponse, JSON.toJSONString(ret));
                return new ModelAndView();
            } else {
                //生成OAuth响应
                OAuthResponse response = OAuthASResponse
                        .tokenResponse(HttpServletResponse.SC_OK)
                        .setAccessToken(accessToken)
                        .setExpiresIn(String.valueOf(oAuthClientService.getExpire()))
                        .buildJSONMessage();
                //根据OAuthResponse生成ResponseEntity
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }
        } catch (OAuthProblemException e) {
            if (isAppClientType) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, e.getMessage());
            } else {
                //构建错误响应
                OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e)
                        .buildJSONMessage();
                return new ResponseEntity(res.getBody(), HttpStatus.valueOf(res.getResponseStatus()));
            }
        }
    }

    public Object getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        boolean isAppClientType = validClientType(httpServletRequest);
        try {
            //构建OAuth资源请求
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(httpServletRequest, ParameterStyle.QUERY);
            //获取Access Token
            String accessToken = oauthRequest.getAccessToken();
            //验证Access Token
            String username = oAuthClientService.getUsernameByToken(accessToken);
            if (username == null) {
                if (isAppClientType) {
                    throw new BizErrorEx(OAuthBizErrorCode.OAUTH_TOKEN_CODE_ERROR, "TOKEN验证无效");
                } else {
                    // 如果不存在/过期了，返回未验证错误，需重新验证
                    OAuthResponse oauthResponse = OAuthRSResponse
                            .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                            .setRealm("oauth-server")
                            .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
                            .buildHeaderMessage();
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
                    return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
                }
            }
            //返回用户名
            if (isAppClientType) {
                Map<String, String> map = new HashMap<String, String>();
                // Decipher decipher = new AESDcipher();
                map.put("username", username);
                // map.put("credit", decipher.encode(oAuthClientService.getPasswordByUsername(username)));
                Ret<Map<String, String>> ret = new Ret<Map<String, String>>(Ret.OK_STATUS, Ret.OK_MESSAGE, map);
                HttpUtils.sendJsonMsg(httpServletResponse, JSON.toJSONString(ret));
                return new ModelAndView();
            } else {
                return new ResponseEntity(username, HttpStatus.OK);
            }
        } catch (OAuthProblemException e) {
            if (isAppClientType) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, e.getMessage());
            } else {
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

    @Override
    public Object authorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String redirect_uri = null;
        try {
            //构建OAuth 授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            //检查传入的客户端id是否正确
            if (!oAuthClientService.validClientId(oauthRequest.getClientId())) {
                OAuthResponse oAuthResponse = OAuthASResponse
                        .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .setErrorDescription("client_id参数错误")
                        .buildJSONMessage();
                return new ResponseEntity(
                        oAuthResponse.getBody(), HttpStatus.valueOf(oAuthResponse.getResponseStatus()));
            }
            redirect_uri = oAuthClientService.getClientRedirect(oauthRequest.getClientId());
            Subject subject = SecurityUtils.getSubject();
            //如果用户没有登录，跳转到登陆页面
            if (!subject.isAuthenticated()) {
                try {
                    oauthAutoLogin(subject, request);
                } catch (BizErrorEx ex) {
                    String client_type = request.getParameter("client_type");
                    if (client_type != null && !"app".equals(client_type)) {
                        OAuthResponse oAuthResponse = OAuthASResponse
                                .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_REQUEST)
                                .setErrorDescription("帐号/密码错误")
                                .buildJSONMessage();
                        return new ResponseEntity(
                                oAuthResponse.getBody(), HttpStatus.valueOf(oAuthResponse.getResponseStatus()));
                    } else {
                        // WEB方式登录失败时跳转到登陆页面
                        ModelAndView view = new ModelAndView("index");
                        OAuth2Client oAuth2Client = new OAuth2Client();
                        oAuth2Client.setClient_id(oauthRequest.getClientId());
                        oAuth2Client.setRedirect_uri(redirect_uri);
                        oAuth2Client.setResponse_type(oauthRequest.getResponseType());
                        oAuth2Client.setState(oauthRequest.getState());
                        view.addObject("client", oAuth2Client);
                        view.addObject("errorMsg", ex.getMessage());
                        view.addObject("username", request.getParameter("username"));
                        view.addObject("password", request.getParameter("password"));
                        return view;
                    }
                }
            }
            String username = (String) subject.getPrincipal();
            //生成授权码
            String authorizationCode = null;
            //responseType目前仅支持CODE，另外还有TOKEN
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            if (responseType.equals(ResponseType.CODE.toString())) {
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                authorizationCode = oauthIssuerImpl.authorizationCode();
                oAuthClientService.addAuthCodeAndUsername(authorizationCode, username);
                oAuthClientService.addAuthCodeAndToken(authorizationCode, subject.getSession().getId().toString());
                oAuthClientService.addOAuthUserInfo(username, request.getParameter("password"));
            }
            //进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                    OAuthASResponse.authorizationResponse(request,
                            HttpServletResponse.SC_FOUND);
            //设置授权码
            builder.setCode(authorizationCode);
            if (!StringUtils.isEmpty(oauthRequest.getState())) {
                builder.setParam("state", oauthRequest.getState());
            }
            //构建响应
            final OAuthResponse oAuthResponse = builder.location(redirect_uri).buildQueryMessage();
            //根据OAuthResponse返回ResponseEntity响应
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(oAuthResponse.getLocationUri()));
            return new ResponseEntity(headers, HttpStatus.valueOf(oAuthResponse.getResponseStatus()));
        } catch (OAuthProblemException e) {
            //返回错误消息（如?error=）
            e.printStackTrace();
            if (redirect_uri == null) {
                return new ResponseEntity(
                        "OAuth callback url needs to be provided by client", HttpStatus.NOT_FOUND);
            }
            final OAuthResponse oAuthResponse =
                    OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                            .error(e).location(redirect_uri).buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(oAuthResponse.getLocationUri()));
            return new ResponseEntity(headers, HttpStatus.valueOf(oAuthResponse.getResponseStatus()));
        }
    }

    @Override
    public Object token(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            //构建OAuth请求
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
            //检查提交的客户端id是否正确
            if (!oAuthClientService.validClientId(oauthRequest.getClientId())) {
                OAuthResponse oAuthResponse =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                .setErrorDescription("client_id参数错误")
                                .buildJSONMessage();
                return new ResponseEntity(
                        oAuthResponse.getBody(), HttpStatus.valueOf(oAuthResponse.getResponseStatus()));
            }
            // 检查客户端安全KEY是否正确
            if (!oAuthClientService.validClientSecret(oauthRequest.getClientId(), oauthRequest.getClientSecret())) {
                OAuthResponse oAuthResponse =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                                .setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
                                .setErrorDescription("client_secret参数错误")
                                .buildJSONMessage();
                return new ResponseEntity(oAuthResponse.getBody(), HttpStatus.valueOf(oAuthResponse.getResponseStatus()));
            }
            // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
            String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())) {
                if (!oAuthClientService.validAuthCode(authCode)) {
                    OAuthResponse oAuthResponse = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
                            .setErrorDescription("授权码错误")
                            .buildJSONMessage();
                    return new ResponseEntity(oAuthResponse.getBody(), HttpStatus.valueOf(oAuthResponse.getResponseStatus()));
                }
                if (!oAuthClientService.validTokenByAuthCode(authCode)) {
                    OAuthResponse oAuthResponse = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
                            .setErrorDescription("授权码失效")
                            .buildJSONMessage();
                    return new ResponseEntity(oAuthResponse.getBody(), HttpStatus.valueOf(oAuthResponse.getResponseStatus()));
                }
            }
            // 生成Access Token
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            final String accessToken = oauthIssuerImpl.accessToken();
            // final String accessToken = oAuthClientService.getTokenByAuthCode(authCode);
            String username = oAuthClientService.getUsernameByAuthCode(authCode);
            oAuthClientService.addTokenAndUsername(accessToken, username);
            if (username != null) {
                oAuthClientService.delAuthCodeAndUsername(authCode);
                //生成OAuth响应
                OAuthResponse oAuthResponse = OAuthASResponse
                        .tokenResponse(HttpServletResponse.SC_OK)
                        .setAccessToken(accessToken)
                        .setExpiresIn(String.valueOf(oAuthClientService.getExpire()))
                        .buildJSONMessage();
                //根据OAuthResponse生成ResponseEntity
                return new ResponseEntity(oAuthResponse.getBody(), HttpStatus.valueOf(oAuthResponse.getResponseStatus()));
            }
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
        //构建错误响应
        OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).setError("获取TOKEN失败,请重新授权")
                .buildJSONMessage();
        return new ResponseEntity(res.getBody(), HttpStatus.valueOf(res.getResponseStatus()));
    }

    @Override
    public Object userinfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            //构建OAuth资源请求
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
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
            }
            //返回用户名
            return new ResponseEntity(oAuthUserService.findByUsername(username), HttpStatus.OK);
        } catch (OAuthProblemException e) {
            e.printStackTrace();
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
