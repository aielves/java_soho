package com.soho.shiro.oauth2.aconst;

/**
 * Created by Administrator on 2017/4/30.
 */
public interface OAuthBizErrorCode {

    public static String OAUTH_CLIENT_ID_ERROR = "000101"; // client_id参数错误
    public static String OAUTH_LOGIN_ERROR = "000102"; // OAUTH登录失败
    public static String OAUTH_REDIRECT_URI_ERROR = "000103"; // redirect_uri参数错误
    public static String OAUTH_CLIENT_SECRET_ERROR = "000104"; // client_secret参数错误
    public static String OAUTH_AUTHORIZE_CODE_ERROR = "000105"; // 授权码错误
    public static String OAUTH_AUTHORIZE_CODE_TIMEOUT = "000106"; // 授权码失效
    public static String OAUTH_TOKEN_CODE_ERROR = "000107"; // TOKEN验证无效

}
