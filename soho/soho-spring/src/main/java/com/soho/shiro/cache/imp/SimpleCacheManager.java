package com.soho.shiro.cache.imp;

import com.soho.shiro.cache.Cache;
import com.soho.shiro.cache.CacheManager;

import java.util.Map;

/**
 * 简易缓存管理器实现类
 *
 * @author shadow
 */
public class SimpleCacheManager implements CacheManager {

    private Map<String, Cache> cacheMap;
    private String cacheName;

    public Cache getCache(String cacheName) {
        return cacheMap.get(cacheName);
    }

    public void destroy() {
        cacheMap.remove(cacheName);
    }

    public Map<String, Cache> getCacheMap() {
        return cacheMap;
    }

    public void setCacheMap(Map<String, Cache> cacheMap) {
        this.cacheMap = cacheMap;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

}
