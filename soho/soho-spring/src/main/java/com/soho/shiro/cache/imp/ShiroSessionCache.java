package com.soho.shiro.cache.imp;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.soho.shiro.cache.CacheManager;
import com.soho.web.domain.ProjectInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Set;

/**
 * Shiro缓存实现类
 *
 * @author shadow
 */
public class ShiroSessionCache implements Cache<String, Session> {

    @Autowired(required = false)
    private CacheManager cacheManager;
    @Autowired(required = false)
    private ProjectInfo projectInfo;

    public Session get(String s) throws CacheException {
        return getCache().get(getProjectCode() + CacheManager.SESSION_CACHE + s);
    }

    public Session put(String s, Session session) throws CacheException {
        getCache().put(getProjectCode() + CacheManager.SESSION_CACHE + s, session, (int) session.getTimeout() / 1000);
        return session;
    }

    public Session remove(String s) throws CacheException {
        getCache().remove(getProjectCode() + CacheManager.SESSION_CACHE + s);
        return null;
    }

    public void clear() throws CacheException {
        getCache().clear();
    }

    public int size() {
        return 0;
    }

    public Set<String> keys() {
        return null;
    }

    public Collection<Session> values() {
        return null;
    }

    private com.soho.shiro.cache.Cache getCache() {
        return cacheManager.getCache(CacheManager.SESSION_CACHE);
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private String getProjectCode() {
        if (projectInfo != null && !StringUtils.isEmpty(projectInfo.getCode())) {
            return projectInfo.getCode() + "_";
        }
        return "";
    }

}
