package com.soho.shiro.cache.imp;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;

/**
 * Shiro缓存管理器实现类
 *
 * @author shadow
 */
public class SimpleShiroCacheManager implements CacheManager, Destroyable {

    private Cache<Object, Object> cache;

    public void destroy() throws Exception {
        // TODO nothing
    }

    public Cache<Object, Object> getCache(String name) throws CacheException {
        return cache;
    }

    public Cache<Object, Object> getCache() {
        return cache;
    }

    public void setCache(Cache<Object, Object> cache) {
        this.cache = cache;
    }

}
