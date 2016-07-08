package org.whb.springmvc.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.whb.springmvc.entity.User;
import org.whb.springmvc.service.ICacheService;
/**
 * 缓存服务
 * 通过@CacheXXXX的cacheNames声明使用的缓存名字（类似命名空间），可以是多个，这个cacheNames是事先定义好的，见ehcache.xml。
 * 缓存中的key可以为空，缺省按照方法的所有参数进行组合；如果指定要按照SpEL表达式编写。
 * 
 * 注解@CachePut会执行方法体，并将数据缓存；
 * 注解@CacheEvict会执行方法体，并将数据从缓存中删除；
 * 注解@Cacheable会先从缓存获取数据，如果未命中则执行方法体，并将数据缓存。
 * 
 * @author 
 *
 */
@Service
@CacheConfig(cacheNames = {"user"})
public class CacheServiceImpl implements ICacheService {
    
    private static final Logger G_Logger = LogManager.getLogger("G");

    Set<User> users = new HashSet<User>();

    @CachePut(key = "#user.id")
    public User save(User user) {
        remove(user.getId());
        users.add(user);
        return user;
    }

    @CacheEvict(key = "#id")
    public User remove(final int id) {
        for (User user : users) {
            if (user.getId() == id) {
                users.remove(user);
                return user;
            }
        }
        return null;
    }

    @CacheEvict(allEntries = true)
    public void removeAll() {
        users.clear();
    }

    @Cacheable(key = "#id")
    public User findById(final int id) {
        G_Logger.info("cache miss, invoke find by id, id:" + id);
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}
