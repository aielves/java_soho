package com.soho.codegen.dao.imp;

import com.soho.codegen.dao.DbMessageDAO;
import com.soho.codegen.domain.DbMessage;
import com.soho.mybatis.crud.dao.imp.MyBatisDAOImp;
import org.springframework.stereotype.Repository;

@Repository
public class DbMessageDAOImp extends MyBatisDAOImp<DbMessage> implements DbMessageDAO {
}