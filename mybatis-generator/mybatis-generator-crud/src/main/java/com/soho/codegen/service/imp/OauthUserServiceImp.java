package com.soho.codegen.service.imp;

import com.soho.codegen.dao.OauthUserDAO;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.service.OauthUserService;
import com.soho.mybatis.crud.dao.MyBatisDAO;
import com.soho.mybatis.crud.service.imp.BaseServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OauthUserServiceImp extends BaseServiceImp<OauthUser, Long> implements OauthUserService {
    @Autowired
    private OauthUserDAO oauthuserDAO;

    public MyBatisDAO<OauthUser, Long> getMybatisDAO() {
        return oauthuserDAO;
    }
}