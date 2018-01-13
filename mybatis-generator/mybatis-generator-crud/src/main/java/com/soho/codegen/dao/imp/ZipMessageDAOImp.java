package com.soho.codegen.dao.imp;

import com.soho.codegen.dao.ZipMessageDAO;
import com.soho.codegen.domain.ZipMessage;
import com.soho.mybatis.crud.dao.imp.MyBatisDAOImp;
import org.springframework.stereotype.Repository;

@Repository
public class ZipMessageDAOImp extends MyBatisDAOImp<ZipMessage> implements ZipMessageDAO {
}