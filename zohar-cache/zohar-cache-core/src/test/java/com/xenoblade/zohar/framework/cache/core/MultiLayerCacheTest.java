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
import com.xenoblade.zohar.framework.cache.core.stats.CacheStats;
import com.xenoblade.zohar.framework.cache.core.support.EExpireMode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

    @Autowired
    private RedissonClient redissonClient;

    private MultiLayerCacheConfig multiLayerCacheConfig1;
    private MultiLayerCacheConfig multiLayerCacheConfig2;
    private MultiLayerCacheConfig multiLayerCacheConfig3;
    private MultiLayerCacheConfig multiLayerCacheConfig4;


    @Before
    public void initCacheConfigs() {
        // 测试 CacheManager getCache方法
        FirstCacheConfig firstCacheConfig1 = new FirstCacheConfig(10, 1000, 4, TimeUnit.SECONDS, EExpireMode.WRITE);
        SecondaryCacheConfig secondaryCacheConfig1 = new SecondaryCacheConfig(10, 4, TimeUnit.SECONDS, true, true, 1);
        multiLayerCacheConfig1 = new MultiLayerCacheConfig(firstCacheConfig1, secondaryCacheConfig1, "");

        // 二级缓存可以缓存null,时间倍率是1
        FirstCacheConfig firstCacheConfig2 = new FirstCacheConfig(10, 1000, 5, TimeUnit.SECONDS, EExpireMode.WRITE);
        SecondaryCacheConfig secondaryCacheConfig2 = new SecondaryCacheConfig(3000, 14, TimeUnit.SECONDS, true, true,  1);
        multiLayerCacheConfig2 = new MultiLayerCacheConfig(firstCacheConfig2, secondaryCacheConfig2, "");

        // 二级缓存可以缓存null,时间倍率是10
        FirstCacheConfig firstCacheConfig3 = new FirstCacheConfig(10, 1000, 5, TimeUnit.SECONDS, EExpireMode.WRITE);
        SecondaryCacheConfig secondaryCacheConfig3 = new SecondaryCacheConfig(100, 70, TimeUnit.SECONDS, true, true,  10);
        multiLayerCacheConfig3 = new MultiLayerCacheConfig(firstCacheConfig3, secondaryCacheConfig3, "");

        // 二级缓存不可以缓存null
        FirstCacheConfig firstCacheSetting4 = new FirstCacheConfig(10, 1000, 5, TimeUnit.SECONDS, EExpireMode.WRITE);
        SecondaryCacheConfig secondaryCacheSetting4 = new SecondaryCacheConfig(10, 7, TimeUnit.SECONDS, true,  false, 1);
        multiLayerCacheConfig4 = new MultiLayerCacheConfig(firstCacheSetting4, secondaryCacheSetting4, "");


        String cacheName = "cache-name";
        Cache cache1 = cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        Cache cache2 = cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        Assert.assertEquals(cache1, cache2);

        Cache cache3 = cacheManager.getCache(cacheName, multiLayerCacheConfig2);
        Collection<Cache> caches = cacheManager.getCache(cacheName);
        Assert.assertTrue(caches.size() == 2);
        Assert.assertNotEquals(cache1, cache3);

        log.info("============ Init complete");
    }


    @Test
    public void testCacheExpiration() {
        // 测试 缓存过期时间
        String cacheName = "cache-name-testCacheExpiration";
        String cacheKey1 = "cache-key-testCacheExpiration";
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


    @Test
    public void testGetCacheNullUserAllowNullValueTrue() {
        log.info("测试二级缓存允许为NULL，NULL值时间倍率是10");
        // 测试 缓存过期时间
        String cacheName = "cache-name-testGetCacheNullUserAllowNullValueTrue";
        String cacheKey1 = "cache-key-testGetCacheNullUserAllowNullValueTrue";
        MultiLayerCache cache1 = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig3);
        cache1.get(cacheKey1, () -> initNullCache());
        // 测试一级缓存值不能缓存NULL
        String str1 = cache1.getFirstCache().get(cacheKey1, String.class);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache1.getFirstCache().getNativeCache();
        Assert.assertTrue(str1 == null);
        Assert.assertTrue(0 == nativeCache.asMap().size());

        // 测试二级缓存可以存NULL值，NULL值时间倍率是10
        String st2 = cache1.getSecondCache().get(cacheKey1, String.class);
        RedisCacheKey redisCacheKey = ((RedisCache) cache1.getSecondCache()).getRedisCacheKey(cacheKey1);
        Long ttl = redisTemplate.getExpire(redisCacheKey.getKey());
        Assert.assertTrue(redisTemplate.hasKey(redisCacheKey.getKey()));
        Assert.assertTrue(st2 == null);
        Assert.assertTrue(ttl <= 10);
        sleep(5, TimeUnit.SECONDS);
        st2 = cache1.getSecondCache().get(cacheKey1, String.class);
        Assert.assertTrue(st2 == null);
        cache1.getSecondCache().get(cacheKey1, () -> initNullCache());
        sleep(1, TimeUnit.SECONDS);
        ttl = redisTemplate.getExpire(redisCacheKey.getKey());
        Assert.assertTrue(ttl <= 10 && ttl > 5);

        st2 = cache1.get(cacheKey1, String.class);
        Assert.assertTrue(st2 == null);
    }



    @Test
    public void testGetCacheNullUserAllowNullValueFalse() {
        log.info("测试二级缓存不允许为NULL");
        // 测试 缓存过期时间
        String cacheName = "cache-name-testGetCacheNullUserAllowNullValueFalse";
        String cacheKey1 = "cache-key-testGetCacheNullUserAllowNullValueFalse";
        MultiLayerCache cache1 = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig4);
        cache1.get(cacheKey1, () -> initNullCache());
        // 测试一级缓存值不能缓存NULL
        String str1 = cache1.getFirstCache().get(cacheKey1, String.class);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache1.getFirstCache().getNativeCache();
        Assert.assertTrue(str1 == null);
        Assert.assertTrue(0 == nativeCache.asMap().size());

        // 测试二级缓存不可以存NULL值，NULL值时间倍率是10
        String st2 = cache1.getSecondCache().get(cacheKey1, String.class);
        RedisCacheKey redisCacheKey = ((RedisCache) cache1.getSecondCache()).getRedisCacheKey(cacheKey1);
        Assert.assertTrue(!redisTemplate.hasKey(redisCacheKey.getKey()));
        Assert.assertTrue(st2 == null);
    }

    @Test
    public void testGetType() throws Exception {
        // 测试 缓存过期时间
        String cacheName = "cache-name-testGetType";
        String cacheKey1 = "cache-key-testGetType";
        MultiLayerCache cache1 = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        cache1.get(cacheKey1, () -> null);
        String str1 = cache1.get(cacheKey1, String.class);
        Assert.assertNull(str1);
        sleep(11, TimeUnit.SECONDS);
        cache1.get(cacheKey1, () -> initCache(String.class));

        str1 = cache1.get(cacheKey1, String.class);
        Assert.assertEquals(str1, initCache(String.class));
    }

    @Test
    public void testCacheEvict() throws Exception {
        // 测试 缓存过期时间
        String cacheName = "cache-name-testCacheEvict";
        String cacheKey1 = "cache-testCacheEvict-1";
        String cacheKey2 = "cache-testCacheEvict-2";
        MultiLayerCache cache1 = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        cache1.get(cacheKey1, () -> initCache(String.class));
        cache1.get(cacheKey2, () -> initCache(String.class));
        // 测试删除方法
        cache1.evict(cacheKey1);
        sleep(500);
        String str1 = cache1.get(cacheKey1, String.class);
        String str2 = cache1.get(cacheKey2, String.class);
        Assert.assertNull(str1);
        Assert.assertNotNull(str2);
        // 测试删除方法
        cache1.evict(cacheKey1);
        sleep(500);
        str1 = cache1.get(cacheKey1, () -> initCache(String.class));
        str2 = cache1.get(cacheKey2, String.class);
        Assert.assertNotNull(str1);
        Assert.assertNotNull(str2);
    }

    @Test
    public void testCacheClear() throws Exception {
        // 测试 缓存过期时间
        String cacheName = "cache-name-testCacheClear";
        String cacheKey1 = "cache-testCacheClear-1";
        String cacheKey2 = "cache-testCacheClear-2";
        MultiLayerCache cache = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        cache.get(cacheKey1, () -> initCache(String.class));
        cache.get(cacheKey2, () -> initCache(String.class));
        // 测试清除方法
        cache.clear();
        sleep(500);
        String str1 = cache.get(cacheKey1, String.class);
        String str2 = cache.get(cacheKey2, String.class);
        Assert.assertNull(str1);
        Assert.assertNull(str2);
        // 测试清除方法
        cache.clear();
        sleep(500);
        str1 = cache.get(cacheKey1, () -> initCache(String.class));
        str2 = cache.get(cacheKey2, () -> initCache(String.class));
        Assert.assertNotNull(str1);
        Assert.assertNotNull(str2);
    }

    @Test
    public void testCachePut() throws Exception {
        // 测试 缓存过期时间
        String cacheName = "cache-name-testCachePut";
        String cacheKey1 = "cache-key-testCachePut";
        MultiLayerCache cache = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        String str1 = cache.get(cacheKey1, String.class);
        Assert.assertNull(str1);

        cache.put(cacheKey1, "test1");
        str1 = cache.get(cacheKey1, String.class);
        Assert.assertEquals(str1, "test1");

        cache.put(cacheKey1, "test2");
        sleep(2000);
        Object value = cache.getFirstCache().get(cacheKey1);
        Assert.assertNull(value);
        str1 = cache.get(cacheKey1, String.class);
        Assert.assertEquals(str1, "test2");
    }

    @Test
    public void testPutCacheNullUserAllowNullValueTrue() {
        log.info("测试Put二级缓存允许为NULL，NULL值时间倍率是10");
        // 测试 缓存过期时间
        String cacheName = "cache-name-testPutCacheNullUserAllowNullValueTrue";
        String cacheKey1 = "cache-key-testPutCacheNullUserAllowNullValueTrue";
        MultiLayerCache cache1 = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig3);
        cache1.put(cacheKey1, initNullCache());
        // 测试一级缓存值不能缓存NULL
        String str1 = cache1.getFirstCache().get(cacheKey1, String.class);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache1.getFirstCache().getNativeCache();
        Assert.assertTrue(str1 == null);
        Assert.assertTrue(0 == nativeCache.asMap().size());

        // 测试二级缓存可以存NULL值，NULL值时间倍率是10
        String st2 = cache1.getSecondCache().get(cacheKey1, String.class);
        RedisCacheKey redisCacheKey = ((RedisCache) cache1.getSecondCache()).getRedisCacheKey(cacheKey1);
        Long ttl = redisTemplate.getExpire(redisCacheKey.getKey());
        Assert.assertTrue(redisTemplate.hasKey(redisCacheKey.getKey()));
        Assert.assertTrue(st2 == null);
        Assert.assertTrue(ttl <= 10);
        sleep(5, TimeUnit.SECONDS);
        st2 = cache1.getSecondCache().get(cacheKey1, String.class);
        Assert.assertTrue(st2 == null);
        cache1.getSecondCache().get(cacheKey1, () -> initNullCache());
        sleep(1, TimeUnit.SECONDS);
        ttl = redisTemplate.getExpire(redisCacheKey.getKey());
        Assert.assertTrue(ttl <= 10 && ttl > 5);

        st2 = cache1.get(cacheKey1, String.class);
        Assert.assertTrue(st2 == null);
    }

    @Test
    public void testCacheNullUserAllowNullValueFalse() {
        log.info("测试Put二级缓存不允许为NULL");
        // 测试 缓存过期时间
        String cacheName = "cache-name-testCacheNullUserAllowNullValueFalse";
        String cacheKey1 = "cache-key-testCacheNullUserAllowNullValueFalse";
        MultiLayerCache cache1 = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig4);
        cache1.put(cacheKey1, initNullCache());
        // 测试一级缓存值不能缓存NULL
        String str1 = cache1.getFirstCache().get(cacheKey1, String.class);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache1.getFirstCache().getNativeCache();
        Assert.assertTrue(str1 == null);
        Assert.assertTrue(0 == nativeCache.asMap().size());

        // 测试二级缓存不可以存NULL值，NULL值时间倍率是10
        String st2 = cache1.getSecondCache().get(cacheKey1, String.class);
        RedisCacheKey redisCacheKey = ((RedisCache) cache1.getSecondCache()).getRedisCacheKey(cacheKey1);
        Assert.assertTrue(!redisTemplate.hasKey(redisCacheKey.getKey()));
        Assert.assertTrue(st2 == null);
    }

    @Test
    public void testCachePutIfAbsent() throws Exception {
        // 测试 缓存过期时间
        String cacheName = "cache-name-testCachePutIfAbsent";
        String cacheKey1 = "cache-key-testCachePutIfAbsent";
        MultiLayerCache cache = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        cache.putIfAbsent(cacheKey1, "test1");
        sleep(2000);
        Object value = cache.getFirstCache().get(cacheKey1);
        Assert.assertNull(value);
        String str1 = cache.get(cacheKey1, String.class);
        Assert.assertEquals(str1, "test1");

        cache.putIfAbsent(cacheKey1, "test2");
        str1 = cache.get(cacheKey1, String.class);
        Assert.assertEquals(str1, "test1");
    }


    /**
     * 测试统计
     */
    @Test
    public void testStats() {
        // 测试 缓存过期时间
        String cacheName = "cache-name-testStats";
        String cacheKey1 = "cache-key-testStats";
        MultiLayerCache cache1 = (MultiLayerCache) cacheManager.getCache(cacheName, multiLayerCacheConfig1);
        cache1.get(cacheKey1, () -> initCache(String.class));
        cache1.get(cacheKey1, () -> initCache(String.class));
        sleep(5, TimeUnit.SECONDS);
        cache1.get(cacheKey1, () -> initCache(String.class));

        sleep(11, TimeUnit.SECONDS);
        cache1.get(cacheKey1, () -> initCache(String.class));

        CacheStats cacheStats = cache1.getCacheStats();
        CacheStats cacheStats2 = cache1.getCacheStats();
        Assert.assertEquals(cacheStats.getCacheRequestCount().longValue(), cacheStats2.getCacheRequestCount().longValue());
        Assert.assertEquals(cacheStats.getCachedMethodRequestCount().longValue(), cacheStats2.getCachedMethodRequestCount().longValue());
        Assert.assertEquals(cacheStats.getCachedMethodRequestTime().longValue(), cacheStats2.getCachedMethodRequestTime().longValue());

        log.info("缓请求数：{}", cacheStats.getCacheRequestCount());
        log.info("被缓存方法请求数：{}", cacheStats.getCachedMethodRequestCount());
        log.info("被缓存方法请求总耗时：{}", cacheStats.getCachedMethodRequestTime());

        Assert.assertEquals(cacheStats.getCacheRequestCount().longValue(), 4);
        Assert.assertEquals(cacheStats.getCachedMethodRequestCount().longValue(), 2);
        Assert.assertTrue(cacheStats.getCachedMethodRequestTime().longValue() >= 0);
    }

    /**
     * 测试锁
     */
    @Test
    public void testLock() {
        RLock lock =  redissonClient.getLock("test:123");
        lock.lock();
        lock.unlock();
    }


    private <T> T initCache(Class<T> t) {
        log.info("加载值");
        return (T) "test";
    }

    private <T> T initNullCache() {
        log.info("加载空值");
        return null;
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
