package com.test.service.imp;

import com.soho.mybatis.crud.dao.MyBatisDAO;
import com.soho.mybatis.crud.service.imp.BaseServiceImp;
import com.test.dao.YxUserDAO;
import com.test.domain.YxUser;
import com.test.service.YxUserService;
import org.springframework.beans.factory.annotation.Autowired;

public class YxUserServiceImp extends BaseServiceImp<YxUser, Long> implements YxUserService {
    @Autowired(required = false)
    private YxUserDAO yxuserDAO;

    public MyBatisDAO<YxUser, Long> getMybatisDAO() {
        return yxuserDAO;
    }
}