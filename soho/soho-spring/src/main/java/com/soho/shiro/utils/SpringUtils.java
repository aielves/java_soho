package com.soho.shiro.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext contex) throws BeansException {
        setContext(contex);
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        SpringUtils.context = context;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(Class<T> clazz, String name) {
        return context.getBean(name, clazz);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

}