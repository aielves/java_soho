package com.soho.shiro.oauth2.server;

import com.soho.ex.BizErrorEx;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/5/1.
 */
public interface OAuthAuthzService {

    /**
     * 获取授权码
     *
     * @param : 请求参数: username, password, client_id, client_type[app,web], response_type[code], redirect_uri
     *          返回参数: authorize_code
     * @return JSON Object或HttpEntity
     * @throws Exception
     */
    public Object getAuthorizeCode(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * OAUTH帐号密码登录
     *
     * @param subject
     * @param request
     * @return Boolean
     * @throws BizErrorEx
     */
    public boolean oauthAutoLogin(Subject subject, HttpServletRequest request) throws BizErrorEx;

    /**
     * 获取通行证TOKEN
     *
     * @param : 请求参数: client_id, client_secret, client_type[app,web], grant_type[authorization_code], redirect_uri, code
     *          返回参数: access_token
     * @return JSON Object或HttpEntity
     * @throws Exception
     */
    public Object getAccessToken(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 获取OAUTH认证用户数据
     *
     * @param : 请求参数: access_token
     *          返回参数: [id,username,...]
     * @return JSON Object或HttpEntity
     * @throws Exception
     */
    public Object getUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception;


    /**
     * 获取授权码
     *
     * @param : 请求参数: username, password, client_id, client_type[app,web], response_type[code], redirect_uri
     *          返回参数: authorize_code
     * @return JSON Object或HttpEntity
     * @throws Exception
     */
    public Object authorize(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 获取通行证TOKEN
     *
     * @param : 请求参数: client_id, client_secret, grant_type[authorization_code], redirect_uri, code
     *          返回参数: access_token
     * @return JSON Object或HttpEntity
     * @throws Exception
     */
    public Object token(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 获取OAUTH认证用户数据
     *
     * @param : 请求参数: access_token
     *          返回参数: [id,username,...]
     * @return JSON Object或HttpEntity
     * @throws Exception
     */
    public Object userinfo(HttpServletRequest request, HttpServletResponse response) throws Exception;


}
