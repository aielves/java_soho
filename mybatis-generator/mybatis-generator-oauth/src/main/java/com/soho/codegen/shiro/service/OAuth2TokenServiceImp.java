package com.soho.codegen.shiro.service;

import com.soho.codegen.dao.OauthClientDAO;
import com.soho.codegen.dao.OauthClientTokenDAO;
import com.soho.codegen.dao.OauthUserDAO;
import com.soho.codegen.domain.OauthClient;
import com.soho.codegen.domain.OauthClientToken;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.shiro.error.BizErrorCode;
import com.soho.ex.BizErrorEx;
import com.soho.mybatis.exception.MybatisDAOEx;
import com.soho.mybatis.sqlcode.condition.imp.SQLCnd;
import com.soho.shiro.oauth2.aconst.OAuth2Client;
import com.soho.shiro.oauth2.aconst.OAuth2Token;
import com.soho.shiro.oauth2.service.OAuth2TokenService;
import com.soho.utils.JAQUtils;
import com.soho.utils.MD5Util;
import com.soho.zookeeper.security.imp.AESDcipher;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/18.
 */
@Service
public class OAuth2TokenServiceImp implements OAuth2TokenService {

    @Autowired
    private OauthClientDAO oauthClientDAO;
    @Autowired
    private OauthUserDAO oauthUserDAO;
    @Autowired
    private OauthClientTokenDAO oauthClientTokenDAO;

    @Autowired
    private OAuth2Client oAuth2Client;

    @Override
    public OAuth2Token addClientToken(String clientId, String uid, String username, String code, String token, String refresh_token) throws BizErrorEx {
        try {
            OauthClientToken clientToken = new OauthClientToken();
            clientToken.setUsername(username);
            clientToken.setUid(uid);
            clientToken.setClient_id(clientId);
            clientToken.setCode(code);
            clientToken.setAccess_token(token);
            clientToken.setRefresh_token(refresh_token);
            clientToken.setAccess_time(System.currentTimeMillis());
            clientToken.setRefresh_time(clientToken.getAccess_time());
            clientToken.setCode_expire(clientToken.getAccess_time() + 600000l);
            clientToken.setToken_expire(clientToken.getAccess_time() + 1209600000l);
            clientToken.setCtime(clientToken.getAccess_time());
            oauthClientTokenDAO.insert(clientToken);
            return toOAuth2Token(clientToken);
        } catch (MybatisDAOEx ex) {
            ex.printStackTrace();
            throw new BizErrorEx(BizErrorCode.OAUTH_TOKEN_NULL, "令牌生成失败,请重新尝试");
        }
    }

    @Override
    public OAuth2Token validAccessPbk(String access_token, String access_pbk) throws BizErrorEx {
        if (StringUtils.isEmpty(access_pbk)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_TOKEN_NULL, "令牌公钥为空");
        }
        OAuth2Token oAuth2Token = getOAuth2Token(access_token);
        String appId = oAuth2Token.getClient_id();
        String token = oAuth2Token.getAccess_token();
        if (!buildAccessPbk(appId, token).equals(access_pbk)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_TOKEN_NULL, "令牌公钥无效");
        }
        return oAuth2Token;
    }

    @Override
    public OAuth2Client validClientId(String clientId) throws BizErrorEx {
        OauthClient client = getOauthClient(clientId);
        OAuth2Client oAuth2Client = new OAuth2Client();
        oAuth2Client.setClient_id(client.getClient_id());
        oAuth2Client.setClient_secret(client.getClient_secret());
        return oAuth2Client;
    }

    @Override
    public void validClientSecret(String appKey, String clientSecret) throws BizErrorEx {
        if (StringUtils.isEmpty(appKey) || !appKey.equals(clientSecret)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "客户端密钥校验失败");
        }
    }

    @Override
    public OAuth2Token getTokenByCode(String client_id, String authCode) throws BizErrorEx {
        if (StringUtils.isEmpty(authCode)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CODE_NULL, "授权码不能为空");
        }
        try {
            OauthClientToken token = oauthClientTokenDAO.findOneByCnd(new SQLCnd().eq("client_id", client_id).eq("code", authCode));
            if (token == null) {
                throw new BizErrorEx(BizErrorCode.OAUTH_CODE_ILLEGAL, "授权码不存在");
            }
            if (token.getCode_state() != 1) {
                throw new BizErrorEx(BizErrorCode.OAUTH_CODE_INVALID, "授权码已失效");
            }
            if (token.getCode_expire() < System.currentTimeMillis()) {
                throw new BizErrorEx(BizErrorCode.OAUTH_CODE_EXPIRED, "授权码已过期");
            }
            return delAuthCode(token.getAccess_token());
        } catch (MybatisDAOEx ex) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "系统繁忙,请重新尝试");
        }
    }

    @Override
    public OAuth2Token delAuthCode(String access_token) throws BizErrorEx {
        try {
            OauthClientToken clientToken = oauthClientTokenDAO.findOneByCnd(new SQLCnd().eq("access_token", access_token));
            clientToken.setCode_state(2);
            clientToken.setUtime(System.currentTimeMillis());
            oauthClientTokenDAO.update(clientToken);
            return toOAuth2Token(clientToken);
        } catch (MybatisDAOEx ex) {
            ex.printStackTrace();
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "系统繁忙,请重新尝试");
    }

    @Override
    public String validRredirectUri(String clientId, String redirect_uri) throws BizErrorEx {
        OauthClient client = getOauthClient(clientId);
        if (StringUtils.isEmpty(redirect_uri)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "客户端回调地址参数为空");
        }
        if (StringUtils.isEmpty(client.getDomain_uri())) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "客户端尚未绑定第三方域名");
        }
        if (StringUtils.isEmpty(redirect_uri) || !redirect_uri.startsWith(client.getDomain_uri())) { // 客户端回调地址为空或与客户端主域名不匹配
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "客户端回调地址与第三方提供域名不匹配");
        }
        return client.getDomain_uri();
    }

    @Override
    public String validState(String state) throws BizErrorEx {
        if (StringUtils.isEmpty(state)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "客户端state参数为空");
        }
        return state;
    }

    @Override
    public String validResponseType(String responseType) throws BizErrorEx {
        // responseType目前仅支持CODE，另外还有TOKEN
        if (!ResponseType.CODE.toString().equals(responseType)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "ResponseType参数不支持");
        }
        return responseType;
    }

    @Override
    public String validGrantType(String grantType) throws BizErrorEx {
        // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
        if (!GrantType.AUTHORIZATION_CODE.toString().equals(grantType)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "GrantType参数不支持");
        }
        return grantType;
    }

    @Override
    public OAuth2Token getOAuth2Token(String access_token) throws BizErrorEx {
        if (StringUtils.isEmpty(access_token)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_TOKEN_NULL, "令牌不能为空");
        }
        try {
            OauthClientToken token = oauthClientTokenDAO.findOneByCnd(new SQLCnd().eq("access_token", access_token));
            if (token == null) {
                throw new BizErrorEx(BizErrorCode.OAUTH_TOKEN_ILLEGAL, "令牌不存在");
            }
            if (token.getToken_state() != 1) {
                throw new BizErrorEx(BizErrorCode.OAUTH_TOKEN_INVALID, "令牌已失效");
            }
            if (token.getToken_expire() < System.currentTimeMillis()) {
                throw new BizErrorEx(BizErrorCode.OAUTH_TOKEN_EXPIRED, "令牌已过期");
            }
            return toOAuth2Token(token);
        } catch (MybatisDAOEx ex) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "系统繁忙,请重新尝试");
        }
    }

    @Override
    public Map<String, String> getOauthUser(String uid) throws BizErrorEx {
        try {
            OauthUser user = oauthUserDAO.findOneByCnd(new SQLCnd().eq("uid", uid));
            if (user != null) {
                Map<String, String> map = new HashMap<>();
                map.put("id", user.getId().toString());
                map.put("uid", user.getUid());
                map.put("nickname", StringUtils.isEmpty(user.getNickname()) ? "" : user.getNickname());
                return map;
            }
        } catch (MybatisDAOEx ex) {
            ex.printStackTrace();
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "没有用户数据");
    }

    @Override
    public OAuth2Token logoutToken(String access_token, String access_pbk) throws BizErrorEx {
        try {
            OAuth2Token oAuth2Token = validAccessPbk(access_token, access_pbk);
            OauthClientToken token = oauthClientTokenDAO.findOneByCnd(new SQLCnd().eq("access_token", oAuth2Token.getAccess_token()));
            token.setToken_state(2);
            token.setLogout_time(System.currentTimeMillis());
            token.setUtime(token.getLogout_time());
            oauthClientTokenDAO.update(token);
            return oAuth2Token;
        } catch (MybatisDAOEx ex) {
            throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "系统繁忙,请重新尝试");
        }
    }

    @Override
    public OAuth2Token refreshToken(String clientId, String refreshToken, String accessToken, String accessPbk) throws BizErrorEx {
        if (!buildAccessPbk(clientId, accessToken).equals(accessPbk)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_TOKEN_NULL, "令牌公钥无效");
        }
        try {
            OauthClientToken clientToken = oauthClientTokenDAO.findOneByCnd(new SQLCnd().eq("client_id", clientId).eq("access_token", accessToken).eq("refresh_token", refreshToken).eq("state", 1));
            if (clientToken != null) {
                long refresh_time = System.currentTimeMillis();
                clientToken.setToken_state(1);
                clientToken.setToken_expire(refresh_time + 1209600000l);
                clientToken.setRefresh_time(refresh_time);
                clientToken.setUtime(refresh_time);
                oauthClientTokenDAO.update(clientToken);
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "续期TOKEN失败");
    }

    @Override
    public Map<String, String> loginByUsername(String username, String password) throws BizErrorEx {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BizErrorEx(BizErrorCode.OAUTH_LOGIN_NULL, "帐号/密码不能为空");
        }
        try {
            OauthUser user = oauthUserDAO.findOneByCnd(new SQLCnd().eq("password", AESDcipher.encrypt(password)).eq("username", username));
            if (user != null) {
                Map<String, String> map = new HashMap<>();
                map.put("uid", user.getUid());
                return map;
            }
        } catch (MybatisDAOEx ex) {
            ex.printStackTrace();
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_LOGIN_ERROR, "帐号/密码错误");
    }

    @Override
    public String buildAccessPbk(String appId, String access_token) {
        String key = "pZT!dl%8";
        StringBuffer buffer = new StringBuffer();
        buffer.append("appId=").append(appId).append("&access_token=").append(access_token).append("&key=").append(key);
        return MD5Util.MD5LC(buffer.toString());
    }

    @Override
    public String getOAuth2LoginView() {
        return "oauth2/login";
    }

    @Override
    public String getOAuth2DomainUri() {
        return oAuth2Client.getDomain_uri();
    }

    @Override
    public void validJaqState() throws BizErrorEx {
        if (!JAQUtils.toStateByValid()) {
            throw new BizErrorEx(BizErrorCode.OAUTH_LOGIN_ERROR, "JAQ认证失败");
        }
    }

    @Override
    public void delJaqState() {
        JAQUtils.toStateByRemove();
    }

    private OauthClient getOauthClient(String clientId) throws BizErrorEx {
        if (!StringUtils.isEmpty(clientId)) {
            try {
                OauthClient client = oauthClientDAO.findOneByCnd(new SQLCnd().eq("client_id", clientId));
                if (client != null) {
                    return client;
                }
            } catch (MybatisDAOEx ex) {
                ex.printStackTrace();
            }
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_CLIENT_ERROR, "客户端公钥校验失败");
    }

    private OAuth2Token toOAuth2Token(OauthClientToken clientToken) {
        OAuth2Token oAuth2Token = new OAuth2Token();
        oAuth2Token.setUid(clientToken.getUid());
        oAuth2Token.setUsername(clientToken.getUsername());
        oAuth2Token.setClient_id(clientToken.getClient_id());
        oAuth2Token.setCode(clientToken.getCode());
        oAuth2Token.setAccess_token(clientToken.getAccess_token());
        oAuth2Token.setRefresh_token(clientToken.getRefresh_token());
        oAuth2Token.setAccess_time(clientToken.getAccess_time());
        oAuth2Token.setRefresh_time(clientToken.getRefresh_time());
        oAuth2Token.setCode_expire(clientToken.getCode_expire());
        oAuth2Token.setToken_expire(clientToken.getToken_expire());
        oAuth2Token.setToken_state(clientToken.getToken_state());
        oAuth2Token.setCode_state(clientToken.getCode_state());
        oAuth2Token.setLogout_time(clientToken.getLogout_time());
        return oAuth2Token;
    }

}
