package com.soho.shiro.cache.imp;

import com.soho.shiro.cache.Cache;
import net.rubyeye.xmemcached.MemcachedClient;

import java.util.Collection;
import java.util.Set;

public class XMemcacheCache implements Cache {

    private String project_code = "default_";

    private MemcachedClient memcachedClient;

    public MemcachedClient getMemcachedClient() {
        return memcachedClient;
    }

    public void setMemcachedClient(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public <V> V get(Object key) {
        try {
            return memcachedClient.get(project_code + key.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <V> boolean put(Object key, V value, int exp) {
        try {
            return memcachedClient.set(project_code + key.toString(), exp, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public <V> boolean put(Object key, V value) {
        try {
            return memcachedClient.set(project_code + key.toString(), 0, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(Object key) {
        try {
            return memcachedClient.delete(project_code + key.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void clear() {
        try {
            memcachedClient.flushAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long size() {
        // throw new CacheException("unsupport the size method...");
        return 0;
    }

    public Set<Object> keys() {
        // throw new CacheException("unsupport the keys method...");
        return null;
    }

    public <V> Collection<V> values() {
        // throw new CacheException("unsupport the values method...");
        return null;
    }

    public Object getInstance() {
        return memcachedClient;
    }

    public Class<?> getInstanceClassType() {
        return memcachedClient.getClass();
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }
}
