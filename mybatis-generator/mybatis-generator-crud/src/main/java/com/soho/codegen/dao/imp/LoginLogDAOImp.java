package com.soho.codegen.dao.imp;

import com.soho.codegen.dao.LoginLogDAO;
import com.soho.codegen.domain.LoginLog;
import com.soho.mybatis.crud.dao.imp.MyBatisDAOImp;
import org.springframework.stereotype.Repository;

@Repository
public class LoginLogDAOImp extends MyBatisDAOImp<LoginLog> implements LoginLogDAO {
}