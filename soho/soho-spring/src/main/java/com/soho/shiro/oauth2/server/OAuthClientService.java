package com.soho.shiro.oauth2.server;

/**
 * Created by Administrator on 2017/5/1.
 */
public interface OAuthClientService {

    /**
     * 保存授权码和用户ID的关系
     *
     * @param authCode 授权码
     * @param username 登陆ID
     */
    public void addAuthCodeAndUsername(String authCode, String username);

    /**
     * 删除授权码和用户ID的关系
     *
     * @param authCode 授权码
     */
    public void delAuthCodeAndUsername(String authCode);

    /**
     * 保存授权码和会话TOKEN的关系
     *
     * @param authCode 授权码
     * @param token    会话ID
     */
    public void addAuthCodeAndToken(String authCode, String token);

    /**
     * 保存会话TOKEN和用户ID的关系
     *
     * @param token    会话TOKEN
     * @param username 用户ID
     */
    public void addTokenAndUsername(String token, String username);

    /**
     * 校验client_id参数是否正常
     *
     * @param clientId
     * @return Boolean
     */
    public boolean validClientId(String clientId);

    /**
     * 校验client_secret参数是否正常
     *
     * @param clientId
     * @param clientSecret
     * @return Boolean
     */
    public boolean validClientSecret(String clientId, String clientSecret);

    /**
     * 校验authCode是否有效
     *
     * @param authCode
     * @return Boolean
     */
    public boolean validAuthCode(String authCode);

    /**
     * 校验会话TOKEN是否有效
     *
     * @param authCode
     * @return Boolean
     */
    public boolean validTokenByAuthCode(String authCode);

    /**
     * 通过authCode获取Token
     *
     * @param authCode
     * @return String
     */
    public String getTokenByAuthCode(String authCode);

    /**
     * 通过authCode获取Username
     *
     * @param authCode
     * @return String
     */
    public String getUsernameByAuthCode(String authCode);

    /**
     * 通过TOKEN获取用户ID
     *
     * @param token
     * @return String
     */
    public String getUsernameByToken(String token);

    // 存放clientId对应回调地址
    public void putClientRedirect(String clientId, String uri);

    // 获取clientId对应的回调地址
    public String getClientRedirect(String clientId);

    public long getExpire();

    public void setExpire(long expire);

    public void addOAuthClient(String clientId, String clientSecret);

    public void addOAuthUserInfo(String username, String password);

    public String getPasswordByUsername(String username);

}
