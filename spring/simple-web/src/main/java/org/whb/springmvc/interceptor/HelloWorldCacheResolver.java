package org.whb.springmvc.interceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

/**
 * Cache解析器，用于根据实际情况来动态解析使用哪些Cache。
 * context.getOperation().getCacheNames()得到当前目标对象/目标方法上配置的cacheNames；然后可以在此基础上添加额外的cache。
 * 
 * @author 
 *
 */
public class HelloWorldCacheResolver implements CacheResolver {
    
    private CacheManager cacheManager;

    public HelloWorldCacheResolver(CacheManager cacheManager) {
        super();
        this.cacheManager = cacheManager;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        List<Cache> caches = new ArrayList<Cache>();
        for(String cacheName : context.getOperation().getCacheNames()) {
            caches.add(cacheManager.getCache(cacheName));
        }
        return caches;
    }

}
