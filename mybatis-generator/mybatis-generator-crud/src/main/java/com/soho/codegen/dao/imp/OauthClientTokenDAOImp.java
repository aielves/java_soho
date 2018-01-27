package com.soho.codegen.dao.imp;

import com.soho.codegen.dao.OauthClientTokenDAO;
import com.soho.codegen.domain.OauthClientToken;
import com.soho.mybatis.crud.dao.imp.MyBatisDAOImp;
import org.springframework.stereotype.Repository;

@Repository
public class OauthClientTokenDAOImp extends MyBatisDAOImp<OauthClientToken> implements OauthClientTokenDAO {
}