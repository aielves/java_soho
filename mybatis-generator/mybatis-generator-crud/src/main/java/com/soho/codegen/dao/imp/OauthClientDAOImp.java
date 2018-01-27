package com.soho.codegen.dao.imp;

import com.soho.codegen.dao.OauthClientDAO;
import com.soho.codegen.domain.OauthClient;
import com.soho.mybatis.crud.dao.imp.MyBatisDAOImp;
import org.springframework.stereotype.Repository;

@Repository
public class OauthClientDAOImp extends MyBatisDAOImp<OauthClient> implements OauthClientDAO {
}