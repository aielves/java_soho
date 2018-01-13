package com.soho.shiro.oauth2.server.imp;

import com.soho.shiro.cache.Cache;
import com.soho.shiro.cache.CacheManager;
import com.soho.shiro.oauth2.server.OAuthClientService;
import org.springframework.beans.factory.annotation.Autowired;

public class OAuthClientServiceImp implements OAuthClientService {

    public static final String AUTHCODE_USERNAME = "code_usr_";
    public static final String AUTHCODE_TOKEN = "code_token_";
    public static final String TOKEN_USERNAME = "token_usr_";
    public static final String CLIENT = "client_";
    public static final String USERINFO = "userinfo_";
    public static final String CLIENTURL = "clienturl_";

    private long expire = 86400000l;

    @Autowired(required = false)
    private CacheManager cacheManager;

    private Cache getCache() {
        return cacheManager.getCache(CacheManager.DEFAULT_CACHE);
    }

    public void addAuthCodeAndUsername(String authCode, String username) {
        getCache().put(AUTHCODE_USERNAME + authCode, username, (int) expire / 1000);
    }

    public void delAuthCodeAndUsername(String authCode) {
        getCache().remove(AUTHCODE_USERNAME + authCode);
    }

    public void addAuthCodeAndToken(String authCode, String token) {
        getCache().put(AUTHCODE_TOKEN + authCode, token, (int) expire / 1000);
    }

    public void addTokenAndUsername(String token, String username) {
        getCache().put(TOKEN_USERNAME + token, username, (int) expire / 1000);
    }

    public boolean validClientId(String clientId) {
        return getCache().get(CLIENT + clientId) != null ? true : false;
    }

    public boolean validClientSecret(String clientId, String clientSecret) {
        String client_secret = getCache().get(CLIENT + clientId);
        if (client_secret != null && client_secret.equals(clientSecret)) {
            return true;
        }
        return false;
    }

    public boolean validAuthCode(String authCode) {
        return getCache().get(AUTHCODE_USERNAME + authCode) != null ? true : false;
    }

    public boolean validTokenByAuthCode(String authCode) {
        return getCache().get(AUTHCODE_TOKEN + authCode) != null ? true : false;
    }

    public String getTokenByAuthCode(String authCode) {
        return getCache().get(AUTHCODE_TOKEN + authCode);
    }

    public String getUsernameByAuthCode(String authCode) {
        return getCache().get(AUTHCODE_USERNAME + authCode);
    }

    public String getUsernameByToken(String token) {
        return getCache().get(TOKEN_USERNAME + token);
    }

    @Override
    public void putClientRedirect(String clientId, String uri) {
        getCache().put(CLIENTURL + clientId, uri);
    }

    @Override
    public String getClientRedirect(String clientId) {
        return getCache().get(CLIENTURL + clientId);
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public void addOAuthClient(String clientId, String clientSecret) {
        getCache().put(CLIENT + clientId, clientSecret);
    }

    public void addOAuthUserInfo(String username, String password) {
        getCache().put(USERINFO + username, password);
    }

    public String getPasswordByUsername(String username) {
        return getCache().get(USERINFO + username);
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
