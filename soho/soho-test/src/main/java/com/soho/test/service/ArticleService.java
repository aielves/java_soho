package com.soho.test.service;

import java.util.List;

import com.soho.mybatis.pageable.Pagination;
import com.soho.mybatis.sqlcode.condition.Cnd;
import com.soho.test.domain.Article;

public interface ArticleService {

	public List<Article> find(Cnd cnd);

	public Pagination<Article> findByPage(Cnd cnd);

}
