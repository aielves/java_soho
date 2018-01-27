package com.soho.shiro.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

// Shiro框架用户会话信息
public class SessionUtils {

    public final static String USER = "session_user";

    // 获取认证对象
    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    // 获取会话信息
    public static Session getSession() {
        return getSubject().getSession();
    }

    // 获取登录用户信息
    public static Object getUser() {
        return getAttribute(USER);
    }

    // 设置当前登录用户信息
    public static Object setUser(Object userinfo) {
        setAttribute(USER, userinfo);
        return userinfo;
    }

    // 用户会话快速设置键值对
    public static void setAttribute(Object key, Object value) {
        getSession().setAttribute(key, value);
    }

    // 用户会话快速获取键值对
    public static Object getAttribute(Object key) {
        return getSession().getAttribute(key);
    }

    // 用户会话快速删除键值对
    public static Object removeAttribute(Object key) {
        return getSession().removeAttribute(key);
    }

}
