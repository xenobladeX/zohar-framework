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
import com.xenoblade.zohar.framework.cache.core.support.ECacheMode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
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
     * redisson 客户端
     */
    private RedissonClient redissonClient;

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

    private ECacheMode cacheMode = ECacheMode.ALL;


    /**
     * 创建一个多级缓存对象
     *
     * @param redissonClient       redisson客户端
     * @param firstCache           一级缓存
     * @param secondCache          二级缓存
     * @param stats                是否开启统计
     * @param multiLayerCacheConfig 多级缓存配置
     */
    public MultiLayerCache(RedissonClient redissonClient, AbstractValueAdaptingCache firstCache,
                         AbstractValueAdaptingCache secondCache, boolean stats, MultiLayerCacheConfig multiLayerCacheConfig) {
        this(redissonClient, firstCache, secondCache, true, stats, secondCache.getName(), multiLayerCacheConfig);
    }

    /**
     * @param redissonClient       redisson客户端
     * @param firstCache           一级缓存
     * @param secondCache          二级缓存
     * @param useFirstCache        是否使用一级缓存，默认是
     * @param stats                是否开启统计，默认否
     * @param name                 缓存名称
     * @param multiLayerCacheConfig 多级缓存配置
     */
    public MultiLayerCache(RedissonClient redissonClient, AbstractValueAdaptingCache firstCache,
                         AbstractValueAdaptingCache secondCache, boolean useFirstCache, boolean stats, String name, MultiLayerCacheConfig multiLayerCacheConfig) {
        super(stats, name);
        this.redissonClient = redissonClient;
        this.firstCache = firstCache;
        this.secondCache = secondCache;
        this.useFirstCache = useFirstCache;
        this.multiLayerCacheConfig = multiLayerCacheConfig;
        this.cacheMode = multiLayerCacheConfig.getCacheMode();
    }

    @Override
    public MultiLayerCache getNativeCache() {
        return this;
    }

    @Override
    public Object get(Object key) {
        Object result = null;
        switch (cacheMode) {
            case NONE:
            {

                break;
            }
            case ALL:
            {
                result = firstCache.get(key);
                if (result == null) {
                    result = secondCache.get(key);
                    firstCache.putIfAbsent(key, result);
                }
                break;
            }
            case ONLY_FIRST:
            {
                result = firstCache.get(key);
                break;
            }
            case ONLY_SECOND:
            {
                result = secondCache.get(key);
                break;
            }
            default:
            {

                break;
            }
        }
        return fromStoreValue(result);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T result = null;
        switch (cacheMode) {
            case NONE:
            {

                break;
            }
            case ALL:
            {
                result = firstCache.get(key, type);
                if (result != null) {
                    result = (T)fromStoreValue(result);
                } else {
                    result = secondCache.get(key, type);
                    firstCache.putIfAbsent(key, result);
                }
                break;
            }
            case ONLY_FIRST:
            {
                result = firstCache.get(key, type);
                if (result != null) {
                    result = (T)fromStoreValue(result);
                }
                break;
            }
            case ONLY_SECOND:
            {
                result = secondCache.get(key, type);
                break;
            }
            default: {

                break;
            }

        }
        return result;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T result = null;
        switch (cacheMode) {
            case NONE:
            {
                try {
                    result = valueLoader.call();
                } catch (Exception e) {
                    throw new LoaderCacheValueException(key, e);
                }
                break;
            }
            case ALL:
            {
                Object ret = firstCache.get(key);
                if (ret != null) {
                    result = (T) fromStoreValue(ret);
                } else {
                    result = secondCache.get(key, valueLoader);
                    firstCache.putIfAbsent(key, result);
                }
                break;
            }
            case ONLY_FIRST:
            {
                result = firstCache.get(key, valueLoader);
                break;
            }
            case ONLY_SECOND:
            {
                result = secondCache.get(key, valueLoader);
                break;
            }
            default: {
                try {
                    result = valueLoader.call();
                } catch (Exception e) {
                    throw new LoaderCacheValueException(key, e);
                }
                break;
            }

        }
        return result;
    }

    @Override
    public void put(Object key, Object value) {
        switch (cacheMode) {
            case NONE:
            {

                break;
            }
            case ALL:
            {
                secondCache.put(key, value);
                // 删除一级缓存
                deleteFirstCache(key);
                break;
            }
            case ONLY_FIRST:
            {
                firstCache.put(key, value);
                break;
            }
            case ONLY_SECOND:
            {
                secondCache.put(key, value);
                break;
            }
            default: {

                break;
            }

        }
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        Object result = null;
        switch (cacheMode) {
            case NONE:
            {

                break;
            }
            case ALL:
            {
                result = secondCache.putIfAbsent(key, value);
                // 删除一级缓存
                deleteFirstCache(key);
                break;
            }
            case ONLY_FIRST:
            {
                result = firstCache.putIfAbsent(key, value);
                break;
            }
            case ONLY_SECOND:
            {
                result = secondCache.putIfAbsent(key, value);
                break;
            }
            default: {

                break;
            }

        }
        return result;
    }

    @Override
    public void evict(Object key) {
        switch (cacheMode) {
            case NONE:
            {

                break;
            }
            case ALL:
            {
                // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
                secondCache.evict(key);
                // 删除一级缓存
                deleteFirstCache(key);
                break;
            }
            case ONLY_FIRST:
            {
                deleteFirstCache(key);
                break;
            }
            case ONLY_SECOND:
            {
                secondCache.evict(key);
                break;
            }
            default: {

                break;
            }

        }
    }

    @Override
    public void clear() {
        switch (cacheMode) {
            case NONE:
            {

                break;
            }
            case ALL:
            {
                // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
                secondCache.clear();

                // 清除一级缓存需要用到redis的订阅/发布模式，否则集群中其他服服务器节点的一级缓存数据无法删除
                RedisPubSubMessage message = new RedisPubSubMessage();
                message.setCacheName(getName());
                message.setMessageType(ERedisPubSubMessageType.CLEAR);
                // 发布消息
                RTopic topic = redissonClient.getTopic(getName(), new SerializationCodec());
                topic.publish(message);
                break;
            }
            case ONLY_FIRST:
            {
                // 清除一级缓存需要用到redis的订阅/发布模式，否则集群中其他服服务器节点的一级缓存数据无法删除
                RedisPubSubMessage message = new RedisPubSubMessage();
                message.setCacheName(getName());
                message.setMessageType(ERedisPubSubMessageType.CLEAR);
                // 发布消息
                RTopic topic = redissonClient.getTopic(getName(), new SerializationCodec());
                topic.publish(message);
                break;
            }
            case ONLY_SECOND:
            {
                secondCache.clear();
                break;
            }
            default: {

                break;
            }

        }
    }

    private void deleteFirstCache(Object key) {
        // 删除一级缓存需要用到redis的Pub/Sub（订阅/发布）模式，否则集群中其他服服务器节点的一级缓存数据无法删除
        RedisPubSubMessage message = new RedisPubSubMessage();
        message.setCacheName(getName());
        message.setKey(key);
        message.setMessageType(ERedisPubSubMessageType.EVICT);
        // 发布消息
        RTopic topic = redissonClient.getTopic(getName(), new SerializationCodec());
        topic.publish(message);
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
