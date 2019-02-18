package com.xenoblade.zohar.framework.starter.spring.boot.cache.redisson;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = RedissonApplication.class,
        properties = {
            "spring.redis.redisson.config=classpath:redisson.yaml",
            "spring.redis.timeout=10000",
            "spring.cache.type=redis",
        })
public class RedissonCacheManagerAutoConfigurationTest {

    @Autowired
    private CacheManager cacheManager;
    
    @Test
    public void testApp() {
        String key = "test";
        String value = "test";
        Cache cache = cacheManager.getCache("test");
        cache.put(key, value);
        String getValue = (String)cache.get(key).get();
        Assert.assertEquals(getValue, value);
    }
    
}
