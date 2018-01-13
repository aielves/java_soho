package com.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.soho.mybatis.exception.MybatisDAOEx;
import com.soho.mybatis.sqlcode.aconst.SortBy;
import com.soho.mybatis.sqlcode.condition.imp.SQLCnd;
import com.soho.test.service.ArticleService;
import com.test.dao.YxUserDAO;
import com.test.domain.YxUser;

public class MyRun {

	public static void main(String[] args) throws MybatisDAOEx {
		GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.setValidating(false);
		context.load("classpath*:applicationContext*.xml");
		context.refresh();
		YxUserDAO yxUserDAO = context.getBean(YxUserDAO.class);
		YxUser yxUser = new YxUser();
		yxUser.setSex(1);
		yxUser.setUsername("zhangsan");
		yxUser.setPasswd("123123");
		yxUser.setSex(1);
		yxUser.setCtime(System.currentTimeMillis());
		List<YxUser> users = new ArrayList<YxUser>();
		users.add(yxUser);
		yxUserDAO.insert(users);
		yxUserDAO.delete(2l);
		yxUser.setId(3l);
		yxUser.setUsername("wangwu");
		yxUserDAO.update(yxUser);
		yxUser.setSex(20);
		yxUserDAO.update(new SQLCnd().eq("id", 3).addUpdateKeyValue(new String[]{"age"}, new Object[]{40}));
		yxUserDAO.countByCnd(new SQLCnd().eq("id", 2));
		yxUserDAO.findOneByCnd(new SQLCnd().eq("id", 3));
		yxUserDAO.findByCnd(new SQLCnd().eq("id", 3));
		yxUserDAO.findById(3l);
//		ArticleService service = context.getBean(ArticleService.class);
//		service.find(new SQLCnd()
//				.fields("id")
//				.distinct("id", "title")
//				.or(new SQLCnd().between("id", 1, 2).or(
//						new SQLCnd().eq("id", 3).or(new SQLCnd().between("title", 1, 2), new SQLCnd().in("title", 1))),
//						new SQLCnd().in("title", 1, 2, 3)).or(new SQLCnd().eq("id", 1)).groupby("id", "title")
//				.orderby("id", SortBy.A).orderby("id", SortBy.D));
//
//		service.find(new SQLCnd().eq("id", 1));
//		long beginTime = System.currentTimeMillis();
//		service.findByPage(new SQLCnd().fields("id", "title").eq("id", 2).limit(2, 10));
//		System.out.println("=====" + (System.currentTimeMillis() - beginTime));
	}
}
