package com.soho.codegen.service.imp;

import com.soho.codegen.dao.OauthClientDAO;
import com.soho.codegen.domain.OauthClient;
import com.soho.codegen.service.OauthClientService;
import com.soho.mybatis.crud.dao.MyBatisDAO;
import com.soho.mybatis.crud.service.imp.BaseServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OauthClientServiceImp extends BaseServiceImp<OauthClient, Long> implements OauthClientService {
    @Autowired
    private OauthClientDAO oauthclientDAO;

    public MyBatisDAO<OauthClient, Long> getMybatisDAO() {
        return oauthclientDAO;
    }
}