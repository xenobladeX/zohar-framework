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
package com.xenoblade.zohar.framework.cache.core.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.xenoblade.zohar.framework.cache.core.cache.AbstractValueAdaptingCache;
import com.xenoblade.zohar.framework.cache.core.config.FirstCacheConfig;
import com.xenoblade.zohar.framework.cache.core.support.EExpireMode;
import com.xenoblade.zohar.framework.commons.utils.jackson.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.NullValue;

import java.util.concurrent.Callable;

/**
 * CaffeineCache
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class CaffeineCache extends AbstractValueAdaptingCache{

    /**
     * 缓存对象
     */
    private final Cache<Object, Object> cache;

    /**
     * 使用name和{@link FirstCacheConfig}创建一个 {@link CaffeineCache} 实例
     *
     * @param name              缓存名称
     * @param firstCacheConfig 一级缓存配置 {@link FirstCacheConfig}
     * @param stats             是否开启统计模式
     */
    public CaffeineCache(String name, FirstCacheConfig firstCacheConfig, boolean stats) {

        super(stats, name);
        this.cache = getCache(firstCacheConfig);
    }

    @Override
    public Cache<Object, Object> getNativeCache() {
        return this.cache;
    }

    @Override
    public Object get(Object key) {
        log.debug("caffeine缓存 key={} 获取缓存", JacksonUtil.toJson(key));

        if (isStats()) {
            getCacheStats().addCacheRequestCount(1);
        }

        if (this.cache instanceof LoadingCache) {
            return ((LoadingCache<Object, Object>) this.cache).get(key);
        }
        return cache.getIfPresent(key);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        log.debug("caffeine缓存 key={} 获取缓存， 如果没有命中就走库加载缓存", JacksonUtil.toJson(key));

        if (isStats()) {
            getCacheStats().addCacheRequestCount(1);
        }

        Object result = this.cache.get(key, (k) -> loaderValue(key, valueLoader));
        // 如果不允许存NULL值 直接删除NULL值缓存
        boolean isEvict = !isAllowNullValues() && (result == null || result instanceof NullValue);
        if (isEvict) {
            evict(key);
        }
        return (T) fromStoreValue(result);
    }

    @Override
    public void put(Object key, Object value) {
        // 允许存NULL值
        if (isAllowNullValues()) {
            log.debug("caffeine缓存 key={} put缓存，缓存值：{}", JacksonUtil.toJson(key),  JacksonUtil.toJson(value));
            this.cache.put(key, toStoreValue(value));
            return;
        }

        // 不允许存NULL值
        if (value != null && value instanceof NullValue) {
            log.debug("caffeine缓存 key={} put缓存，缓存值：{}", JacksonUtil.toJson(key),  JacksonUtil.toJson(value));
            this.cache.put(key, toStoreValue(value));
        }
        log.debug("缓存值为NULL并且不允许存NULL值，不缓存数据");
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        log.debug("caffeine缓存 key={} putIfAbsent 缓存，缓存值：{}",  JacksonUtil.toJson(key),  JacksonUtil.toJson(value));
        boolean flag = !isAllowNullValues() && (value == null || value instanceof NullValue);
        if (flag) {
            return null;
        }
        Object result = this.cache.get(key, k -> toStoreValue(value));
        return fromStoreValue(result);
    }

    @Override
    public void evict(Object key) {
        log.debug("caffeine缓存 key={} 清除缓存", JacksonUtil.toJson(key));
        this.cache.invalidate(key);
    }

    @Override
    public void clear() {
        log.debug("caffeine缓存 key={} 清空缓存");
        this.cache.invalidateAll();
    }

    /**
     * 加载数据
     */
    private <T> Object loaderValue(Object key, Callable<T> valueLoader) {
        long start = System.currentTimeMillis();
        if (isStats()) {
            getCacheStats().addCachedMethodRequestCount(1);
        }

        try {
            T t = valueLoader.call();
            log.debug("caffeine缓存 key={} 从库加载缓存",  JacksonUtil.toJson(key), JacksonUtil.toJson(t));

            if (isStats()) {
                getCacheStats().addCachedMethodRequestTime(System.currentTimeMillis() - start);
            }
            return toStoreValue(t);
        } catch (Exception e) {
            throw new LoaderCacheValueException(key, e);
        }

    }

    /**
     * 根据配置获取本地缓存对象
     *
     * @param firstCacheConfig 一级缓存配置
     * @return {@link Cache}
     */
    private static Cache<Object, Object> getCache(FirstCacheConfig firstCacheConfig) {
        // 根据配置创建Caffeine builder
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder.initialCapacity(firstCacheConfig.getInitialCapacity());
        builder.maximumSize(firstCacheConfig.getMaximumSize());
        if (EExpireMode.WRITE.equals(firstCacheConfig.getExpireMode())) {
            builder.expireAfterWrite(firstCacheConfig.getExpireTime(), firstCacheConfig.getTimeUnit());
        } else if (EExpireMode.ACCESS.equals(firstCacheConfig.getExpireMode())) {
            builder.expireAfterAccess(firstCacheConfig.getExpireTime(), firstCacheConfig.getTimeUnit());
        }
        // 根据Caffeine builder创建 Cache 对象
        return builder.build();
    }

    @Override
    public boolean isAllowNullValues() {
        return false;
    }


}
