package com.soho.codegen.service.imp;

import com.soho.codegen.dao.OauthClientTokenDAO;
import com.soho.codegen.domain.OauthClientToken;
import com.soho.codegen.service.OauthClientTokenService;
import com.soho.mybatis.crud.dao.MyBatisDAO;
import com.soho.mybatis.crud.service.imp.BaseServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OauthClientTokenServiceImp extends BaseServiceImp<OauthClientToken, Long> implements OauthClientTokenService {
    @Autowired
    private OauthClientTokenDAO oauthclienttokenDAO;

    public MyBatisDAO<OauthClientToken, Long> getMybatisDAO() {
        return oauthclienttokenDAO;
    }
}