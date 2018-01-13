package com.soho.codegen.service.imp;

import com.soho.codegen.dao.ZipMessageDAO;
import com.soho.codegen.domain.ZipMessage;
import com.soho.codegen.service.ZipMessageService;
import com.soho.mybatis.crud.dao.MyBatisDAO;
import com.soho.mybatis.crud.service.imp.BaseServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZipMessageServiceImp extends BaseServiceImp<ZipMessage, Long> implements ZipMessageService {
    @Autowired
    private ZipMessageDAO zipmessageDAO;

    public MyBatisDAO<ZipMessage, Long> getMybatisDAO() {
        return zipmessageDAO;
    }
}