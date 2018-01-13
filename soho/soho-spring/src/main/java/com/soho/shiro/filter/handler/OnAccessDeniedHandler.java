package com.soho.shiro.filter.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/4/27.
 */
public interface OnAccessDeniedHandler {

    public boolean isRetJson(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    public void formAuthenOnAccessDenied(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    public void roleAuthenOnAccessDenied(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

}
