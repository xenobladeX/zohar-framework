/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.cache.core;

import com.xenoblade.zohar.framework.cache.core.cache.Cache;
import com.xenoblade.zohar.framework.cache.core.cache.MultiLayerCache;
import com.xenoblade.zohar.framework.cache.core.cache.redis.RedisCache;
import com.xenoblade.zohar.framework.cache.core.cache.redis.RedisCacheKey;
import com.xenoblade.zohar.framework.cache.core.config.FirstCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.MultiLayerCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.SecondaryCacheConfig;
import com.xenoblade.zohar.framework.cache.core.manager.CacheManager;
import com.xenoblade.zohar.framework.cache.core.support.EExpireMode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.thread.ThreadUtil.sleep;

/**
 * MultiLayerCacheTest
 * @author xenoblade
 * @since 1.0.0
 */
// SpringJUnit4ClassRunner在Junit环境下提供Spring TestContext Framework的功能。
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MultiLayerCacheTest.MultiLayerCacheTestApplication.class})
@Slf4j
public class MultiLayerCacheTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private MultiLayerCacheConfig multiLayerCacheConfig1;
    private MultiLayerCacheConfig multiLayerCacheConfig2;
    private MultiLayerCacheConfig multiLayerCacheConfig3;
    private MultiLayerCacheConfig multiLayerCacheConfig4;


    @Before
    public void initCacheConfigs() {
        // 测试 CacheManager getCache方法
        FirstCacheConfig firstCacheConfig1 = new FirstCacheConfig(10, 1000, 4, TimeUnit.SECONDS, EExpireMode.WRITE);
        SecondaryCacheConfig secondaryCacheConfig1 = new SecondaryCacheConfig(10, 4, TimeUnit.SECONDS, true, true, true, 1);
        multiLayerCacheConfig1 = new MultiLayerCacheConfig(firstCacheConfig1, secondaryCacheConfig1, "");

        // 二级缓存可以缓存null,时间倍率是1
        FirstCacheConfig firstCacheConfig2 = new FirstCacheConfig(10, 1000, 5, TimeUnit.SECONDS, EExpireMode.WRITE);
        SecondaryCacheConfig secondaryCacheConfig2 = new SecondaryCacheConfig(3000, 14, TimeUnit.SECONDS, true, true, true, 1);
        multiLayerCacheConfig2 = new MultiLayerCacheConfig(firstCacheConfig2, secondaryCacheConfig2, "");

        // 二级缓存可以缓存null,时间倍率是10
        FirstCacheConfig firstCacheConfig3 = new FirstCacheConfig(10, 1000, 5, TimeUnit.SECONDS, EExpireMode.WRITE);
        SecondaryCacheConfig secondaryCacheConfig3 = new SecondaryCacheConfig(100, 70, TimeUnit.SECONDS, true, true, true, 10);
        multiLayerCacheConfig3 = new MultiLayerCacheConfig(firstCacheConfig3, secondaryCacheConfig3, "");

        // 二级缓存不可以缓存null
        FirstCacheConfig firstCacheSetting4 = new FirstCacheConfig(10, 1000, 5, TimeUnit.SECONDS, EExpireMode.WRITE);
        SecondaryCacheConfig secondaryCacheSetting4 = new SecondaryCacheConfig(10, 7, TimeUnit.SECONDS, true, false, true, 1);
        multiLayerCacheConfig4 = new MultiLayerCacheConfig(firstCacheSetting4, secondaryCacheSetting4, "");


        String cacheName = "cache:name";
        Cache cache1 = cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        Cache cache2 = cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        Assert.assertEquals(cache1, cache2);

        Cache cache3 = cacheManager.getCache(cacheName, multiLayerCacheConfig2);
        Collection<Cache> caches = cacheManager.getCache(cacheName);
        Assert.assertTrue(caches.size() == 2);
        Assert.assertNotEquals(cache1, cache3);
    }


    @Test
    public void testCacheExpiration() {
        // 测试 缓存过期时间
        String cacheName = "cache:name";
        String cacheKey1 = "cache:key1";
        MultiLayerCache cache1 = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        cache1.get(cacheKey1, () -> initCache(String.class));
        // 测试一级缓存值及过期时间
        String str1 = cache1.getFirstCache().get(cacheKey1, String.class);
        String str2 = cache1.getFirstCache().get(cacheKey1, () -> initCache(String.class));
        log.info("========================:{}", str1);
        Assert.assertTrue(str1.equals(str2));
        Assert.assertTrue(str1.equals(initCache(String.class)));
        sleep(5, TimeUnit.SECONDS);
        Assert.assertNull(cache1.getFirstCache().get(cacheKey1, String.class));
        // 看日志是不是走了二级缓存
        cache1.get(cacheKey1, () -> initCache(String.class));

        // 测试二级缓存
        str1 = cache1.getSecondCache().get(cacheKey1, String.class);
        str2 = cache1.getSecondCache().get(cacheKey1, () -> initCache(String.class));
        Assert.assertTrue(str2.equals(str1));
        Assert.assertTrue(str1.equals(initCache(String.class)));
        sleep(5, TimeUnit.SECONDS);
        // 看日志是不是走了自动刷新
        RedisCacheKey redisCacheKey = ((RedisCache) cache1.getSecondCache()).getRedisCacheKey(cacheKey1);
        cache1.get(cacheKey1, () -> initCache(String.class));
        sleep(6, TimeUnit.SECONDS);
        Long ttl = redisTemplate.getExpire(redisCacheKey.getKey());
        log.info("========================ttl 1:{}", ttl);
        Assert.assertNotNull(cache1.getSecondCache().get(cacheKey1));
        sleep(5, TimeUnit.SECONDS);
        ttl = redisTemplate.getExpire(redisCacheKey.getKey());
        log.info("========================ttl 2:{}", ttl);
        Assert.assertNull(cache1.getSecondCache().get(cacheKey1));
    }




















    private <T> T initCache(Class<T> t) {
        log.debug("加载缓存");
        return (T) "test";
    }

    /**
     * MultiLayerCacheTestApplication
     * @author xenoblade
     * @since 1.0.0
     */
    @SpringBootApplication
    public static class MultiLayerCacheTestApplication {

        public static void main(String[] args) {
            SpringApplication.run(MultiLayerCacheTestApplication.class, args);
        }


    }
}
