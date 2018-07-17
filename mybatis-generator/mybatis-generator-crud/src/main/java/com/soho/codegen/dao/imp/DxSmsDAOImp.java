package com.soho.codegen.dao.imp;

import com.soho.codegen.dao.DxSmsDAO;
import com.soho.codegen.domain.DxSms;
import com.soho.mybatis.crud.dao.imp.MyBatisDAOImp;
import org.springframework.stereotype.Repository;

@Repository
public class DxSmsDAOImp extends MyBatisDAOImp<DxSms> implements DxSmsDAO {
}