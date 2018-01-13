package com.test.service.imp;

import com.soho.mybatis.crud.dao.MyBatisDAO;
import com.soho.mybatis.crud.service.imp.BaseServiceImp;
import com.test.dao.YxProductDAO;
import com.test.domain.YxProduct;
import com.test.service.YxProductService;
import org.springframework.beans.factory.annotation.Autowired;

public class YxProductServiceImp extends BaseServiceImp<YxProduct, Long> implements YxProductService {
    @Autowired(required = false)
    private YxProductDAO yxproductDAO;

    public MyBatisDAO<YxProduct, Long> getMybatisDAO() {
        return yxproductDAO;
    }
}