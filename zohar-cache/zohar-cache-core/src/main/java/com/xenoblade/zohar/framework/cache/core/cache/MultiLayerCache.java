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
package com.xenoblade.zohar.framework.cache.core.cache;

import com.xenoblade.zohar.framework.cache.core.config.MultiLayerCacheConfig;
import com.xenoblade.zohar.framework.cache.core.listener.ERedisPubSubMessageType;
import com.xenoblade.zohar.framework.cache.core.listener.RedisPubSubMessage;
import com.xenoblade.zohar.framework.cache.core.listener.RedisPublisher;
import com.xenoblade.zohar.framework.cache.core.stats.CacheStats;
import com.xenoblade.zohar.framework.commons.utils.jackson.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.concurrent.Callable;

/**
 * MultiLayerCache
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class MultiLayerCache extends AbstractValueAdaptingCache {

    /**
     * redis 客户端
     */
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 一级缓存
     */
    private AbstractValueAdaptingCache firstCache;

    /**
     * 二级缓存
     */
    private AbstractValueAdaptingCache secondCache;

    /**
     * 多级缓存配置
     */
    private MultiLayerCacheConfig multiLayerCacheConfig;

    /**
     * 是否使用一级缓存， 默认true
     */
    private boolean useFirstCache = true;


    /**
     * 创建一个多级缓存对象
     *
     * @param redisTemplate        redisTemplate
     * @param firstCache           一级缓存
     * @param secondCache          二级缓存
     * @param stats                是否开启统计
     * @param multiLayerCacheConfig 多级缓存配置
     */
    public MultiLayerCache(RedisTemplate<String, Object> redisTemplate, AbstractValueAdaptingCache firstCache,
                         AbstractValueAdaptingCache secondCache, boolean stats, MultiLayerCacheConfig multiLayerCacheConfig) {
        this(redisTemplate, firstCache, secondCache, true, stats, secondCache.getName(), multiLayerCacheConfig);
    }

    /**
     * @param redisTemplate        redisTemplate
     * @param firstCache           一级缓存
     * @param secondCache          二级缓存
     * @param useFirstCache        是否使用一级缓存，默认是
     * @param stats                是否开启统计，默认否
     * @param name                 缓存名称
     * @param multiLayerCacheConfig 多级缓存配置
     */
    public MultiLayerCache(RedisTemplate<String, Object> redisTemplate, AbstractValueAdaptingCache firstCache,
                         AbstractValueAdaptingCache secondCache, boolean useFirstCache, boolean stats, String name, MultiLayerCacheConfig multiLayerCacheConfig) {
        super(stats, name);
        this.redisTemplate = redisTemplate;
        this.firstCache = firstCache;
        this.secondCache = secondCache;
        this.useFirstCache = useFirstCache;
        this.multiLayerCacheConfig = multiLayerCacheConfig;
    }

    @Override
    public MultiLayerCache getNativeCache() {
        return this;
    }

    @Override
    public Object get(Object key) {
        Object result = null;
        if (useFirstCache) {
            result = firstCache.get(key);
            log.debug("查询一级缓存。 key={},返回值是:{}", key, JacksonUtil.toJson(result));
        }
        if (result == null) {
            result = secondCache.get(key);
            firstCache.putIfAbsent(key, result);
            log.debug("查询二级缓存,并将数据放到一级缓存。 key={},返回值是:{}", key, JacksonUtil.toJson(result));
        }
        return fromStoreValue(result);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        if (useFirstCache) {
            Object result = firstCache.get(key, type);
            log.debug("查询一级缓存。 key={},返回值是:{}", key, JacksonUtil.toJson(result));
            if (result != null) {
                return (T) fromStoreValue(result);
            }
        }

        T result = secondCache.get(key, type);
        firstCache.putIfAbsent(key, result);
        log.debug("查询二级缓存,并将数据放到一级缓存。 key={},返回值是:{}", key, JacksonUtil.toJson(result));
        return result;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        if (useFirstCache) {
            Object result = firstCache.get(key);
            log.debug("查询一级缓存。 key={},返回值是:{}", key, JacksonUtil.toJson(result));
            if (result != null) {
                return (T) fromStoreValue(result);
            }
        }
        T result = secondCache.get(key, valueLoader);
        firstCache.putIfAbsent(key, result);
        log.debug("查询二级缓存,并将数据放到一级缓存。 key={},返回值是:{}", key, JacksonUtil.toJson(result));
        return result;
    }

    @Override
    public void put(Object key, Object value) {
        secondCache.put(key, value);
        // 删除一级缓存
        if (useFirstCache) {
            deleteFirstCache(key);
        }
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        Object result = secondCache.putIfAbsent(key, value);
        // 删除一级缓存
        if (useFirstCache) {
            deleteFirstCache(key);
        }
        return result;
    }

    @Override
    public void evict(Object key) {
        // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
        secondCache.evict(key);
        // 删除一级缓存
        if (useFirstCache) {
            deleteFirstCache(key);
        }
    }

    @Override
    public void clear() {
        // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
        secondCache.clear();
        if (useFirstCache) {
            // 清除一级缓存需要用到redis的订阅/发布模式，否则集群中其他服服务器节点的一级缓存数据无法删除
            RedisPubSubMessage message = new RedisPubSubMessage();
            message.setCacheName(getName());
            message.setMessageType(ERedisPubSubMessageType.CLEAR);
            // 发布消息
            RedisPublisher.publisher(redisTemplate, new ChannelTopic(getName()), message);
        }
    }

    private void deleteFirstCache(Object key) {
        // 删除一级缓存需要用到redis的Pub/Sub（订阅/发布）模式，否则集群中其他服服务器节点的一级缓存数据无法删除
        RedisPubSubMessage message = new RedisPubSubMessage();
        message.setCacheName(getName());
        message.setKey(key);
        message.setMessageType(ERedisPubSubMessageType.EVICT);
        // 发布消息
        RedisPublisher.publisher(redisTemplate, new ChannelTopic(getName()), message);
    }

    /**
     * 获取一级缓存
     *
     * @return FirstCache
     */
    public Cache getFirstCache() {
        return firstCache;
    }

    /**
     * 获取二级缓存
     *
     * @return SecondCache
     */
    public Cache getSecondCache() {
        return secondCache;
    }

    @Override
    public CacheStats getCacheStats() {
        CacheStats cacheStats = new CacheStats();
        cacheStats.addCacheRequestCount(firstCache.getCacheStats().getCacheRequestCount().longValue());
        cacheStats.addCachedMethodRequestCount(secondCache.getCacheStats().getCachedMethodRequestCount().longValue());
        cacheStats.addCachedMethodRequestTime(secondCache.getCacheStats().getCachedMethodRequestTime().longValue());

        firstCache.getCacheStats().addCachedMethodRequestCount(secondCache.getCacheStats().getCacheRequestCount().longValue());

        setCacheStats(cacheStats);
        return cacheStats;
    }

    public MultiLayerCacheConfig getMultiLayerCacheConfig() {
        return multiLayerCacheConfig;
    }

    @Override
    public boolean isAllowNullValues() {
        return secondCache.isAllowNullValues();
    }


}
