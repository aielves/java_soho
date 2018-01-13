package com.soho.shiro.oauth2.server;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;

/**
 * Created by Administrator on 2017/5/1.
 */
public interface OAuthUserService {

    /**
     * 获取授权码
     *
     * @param : 请求参数: username
     *          返回参数: User
     * @return JSON Object或HttpEntity
     * @throws OAuthProblemException
     */
    public Object findByUsername(String username) throws OAuthProblemException;

}
