package com.soho.codegen.service.imp;

import com.soho.codegen.dao.LoginLogDAO;
import com.soho.codegen.domain.LoginLog;
import com.soho.codegen.service.LoginLogService;
import com.soho.mybatis.crud.dao.MyBatisDAO;
import com.soho.mybatis.crud.service.imp.BaseServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImp extends BaseServiceImp<LoginLog, Long> implements LoginLogService {
    @Autowired
    private LoginLogDAO loginlogDAO;

    public MyBatisDAO<LoginLog, Long> getMybatisDAO() {
        return loginlogDAO;
    }
}