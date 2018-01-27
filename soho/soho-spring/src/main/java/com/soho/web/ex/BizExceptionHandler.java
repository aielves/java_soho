package com.soho.web.ex;

import com.alibaba.fastjson.JSON;
import com.soho.ex.BizErrorEx;
import com.soho.shiro.utils.HttpUtils;
import com.soho.web.domain.Ret;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Created by shadow on 2017/4/27.
 */

public class BizExceptionHandler implements HandlerExceptionResolver {

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        ModelAndView view = new ModelAndView();
        Ret<Object> ret = null;
        if (ex instanceof BizErrorEx) {
            BizErrorEx errorEx = (BizErrorEx) ex;
            String msg = errorEx.getMsg();
            if (StringUtils.isEmpty(msg)) {
                msg = errorEx.getMessage();
            }
            ret = new Ret<Object>(errorEx.getErrorCode(), msg, new HashMap());
        } else {
            ret = new Ret<Object>(Ret.UNKNOWN_STATUS, Ret.UNKNOWN_MESSAGE, new HashMap());
        }
        HttpUtils.sendJsonMsg(response, JSON.toJSONString(ret));
        return view;
    }
}
