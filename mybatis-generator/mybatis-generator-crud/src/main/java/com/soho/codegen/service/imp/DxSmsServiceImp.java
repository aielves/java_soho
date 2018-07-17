package com.soho.codegen.service.imp;

import com.soho.codegen.dao.DxSmsDAO;
import com.soho.codegen.domain.DxSms;
import com.soho.codegen.service.DxSmsService;
import com.soho.mybatis.crud.dao.MyBatisDAO;
import com.soho.mybatis.crud.service.imp.BaseServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DxSmsServiceImp extends BaseServiceImp<DxSms, Long> implements DxSmsService {
    @Autowired
    private DxSmsDAO dxsmsDAO;

    public MyBatisDAO<DxSms, Long> getMybatisDAO() {
        return dxsmsDAO;
    }
}