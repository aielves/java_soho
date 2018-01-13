package com.soho.codegen.dao.imp;

import com.soho.codegen.dao.OauthUserDAO;
import com.soho.codegen.domain.OauthUser;
import com.soho.mybatis.crud.dao.imp.MyBatisDAOImp;
import org.springframework.stereotype.Repository;

@Repository
public class OauthUserDAOImp extends MyBatisDAOImp<OauthUser> implements OauthUserDAO {
}