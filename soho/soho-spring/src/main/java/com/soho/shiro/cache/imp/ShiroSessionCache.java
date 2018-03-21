package com.soho.shiro.cache.imp;

import com.soho.shiro.cache.CacheManager;
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

    private String project_code = "default_";

    @Autowired(required = false)
    private CacheManager cacheManager;

    public Session get(String s) throws CacheException {
        return getCache().get(project_code + CacheManager.SESSION_CACHE + s);
    }

    public Session put(String s, Session session) throws CacheException {
        getCache().put(project_code + CacheManager.SESSION_CACHE + s, session, (int) session.getTimeout() / 1000);
        return session;
    }

    public Session remove(String s) throws CacheException {
        getCache().remove(project_code + CacheManager.SESSION_CACHE + s);
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

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

}
