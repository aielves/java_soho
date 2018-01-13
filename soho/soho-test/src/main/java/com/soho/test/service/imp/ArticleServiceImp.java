package com.soho.test.service.imp;

import com.soho.mybatis.exception.MybatisDAOEx;
import com.soho.mybatis.pageable.Pagination;
import com.soho.mybatis.sqlcode.condition.Cnd;
import com.soho.test.dao.ArticleDAO;
import com.soho.test.domain.Article;
import com.soho.test.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ArticleServiceImp implements ArticleService {

    @Autowired(required = false)
    private ArticleDAO articleDAO;

    @Override
    public List<Article> find(Cnd cnd) {
        try {
            return articleDAO.findByCnd(cnd);
        } catch (MybatisDAOEx e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Pagination<Article> findByPage(Cnd cnd) {
        try {
            return articleDAO.pagingByCnd(cnd);
        } catch (MybatisDAOEx e) {
            e.printStackTrace();
        }
        return null;
    }

}
