package com.soho.mybatis.interceptor;

import com.soho.ex.BizErrorEx;
import com.soho.mybatis.crud.domain.IDEntity;
import com.soho.mybatis.interceptor.lock.DistributedLock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by shadow on 2016/3/24.
 */
public class UserLockInterceptor implements MethodInterceptor {

    private String zkUrl;
    private String module;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        DistributedLock lock = null;
        try {
            Object object = invocation.getArguments()[0];
            if (object instanceof IDEntity<?>) {
                IDEntity<?> entity = (IDEntity<?>) object;
                lock = new DistributedLock(zkUrl, module + entity.getId());
                lock.lock();
            }
            return invocation.proceed();
        } catch (Exception e) {
            if (e instanceof DistributedLock.LockException) {
                throw new BizErrorEx("000099", "用户系统繁忙,请稍后再尝试");
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

    public void setModule(String module) {
        this.module = module;
    }
}