package com.soho.codegen.service.imp;

import com.soho.codegen.dao.DbMessageDAO;
import com.soho.codegen.domain.DbMessage;
import com.soho.codegen.service.DbMessageService;
import com.soho.mybatis.crud.dao.MyBatisDAO;
import com.soho.mybatis.crud.service.imp.BaseServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbMessageServiceImp extends BaseServiceImp<DbMessage, Long> implements DbMessageService {
    @Autowired
    private DbMessageDAO dbmessageDAO;

    public MyBatisDAO<DbMessage, Long> getMybatisDAO() {
        return dbmessageDAO;
    }
}