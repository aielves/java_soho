package com.soho.mybatis.interceptor;

import com.soho.ex.BizErrorEx;
import com.soho.mybatis.interceptor.lock.DistributedLock;
import com.soho.shiro.utils.SessionUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.session.Session;

/**
 * Created by shadow on 2016/3/24.
 */
public class ZKLockInterceptor implements MethodInterceptor {

    private String zkUrl;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        DistributedLock lock = null;
        try {
            Session session = SessionUtils.getSession();
            lock = new DistributedLock(zkUrl, session.getId().toString());
            lock.lock();
            // System.out.println("===Thread " + Thread.currentThread().getId() + " running");
            return invocation.proceed();
        } catch (Exception e) {
            if (e instanceof DistributedLock.LockException) {
                throw new BizErrorEx("000100", "系统业务繁忙,请稍后再尝试");
            }
            throw e;
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public void setZkUrl(String zkUrl) {
        this.zkUrl = zkUrl;
    }
}