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
package com.xenoblade.zohar.framework.cache.core.stats;

import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.cache.core.cache.Cache;
import com.xenoblade.zohar.framework.cache.core.cache.MultiLayerCache;
import com.xenoblade.zohar.framework.cache.core.config.MultiLayerCacheConfig;
import com.xenoblade.zohar.framework.cache.core.manager.AbstractCacheManager;
import com.xenoblade.zohar.framework.cache.core.manager.CacheManager;
import com.xenoblade.zohar.framework.cache.core.support.ECacheConstants;
import com.xenoblade.zohar.framework.cache.core.util.RedisLockUtils;
import com.xenoblade.zohar.framework.commons.utils.jackson.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 统计服务
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class CacheStatsService {


    /**
     * {@link AbstractCacheManager }
     */
    private AbstractCacheManager cacheManager;

    private Codec codec = new JsonJacksonCodec();

    /**
     * 缓存统计数据前缀
     */
    private static final String CACHE_STATS_KEY_PREFIX = new StringBuilder("zohar")
            .append(ECacheConstants.REDIS_KEY_INNER_SPLIT)
            .append("multiLayerCache")
            .append(ECacheConstants.REDIS_KEY_INNER_SPLIT)
            .append("cacheStatsInfo")
            .toString();
    /**
     * 定时任务线程池
     */
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(50);

    /**
     * 获取缓存统计list
     *
     * @param cacheNameParam 缓存名称
     * @return List&lt;CacheStatsInfo&gt;
     */
    public List<CacheStatsInfo> listCacheStats(String cacheNameParam) {
        List<CacheStatsInfo> statsList = new ArrayList<>();
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            if (StrUtil.isNotBlank(cacheNameParam) && !cacheName.startsWith(cacheNameParam)) {
                continue;
            }

            // 获取Cache
            Collection<Cache> caches = cacheManager.getCache(cacheName);
            for (Cache cache : caches) {
                MultiLayerCache multiLayerCache = (MultiLayerCache) cache;
                MultiLayerCacheConfig multiLayerCacheConfig = multiLayerCache.getMultiLayerCacheConfig();
                // 加锁并增量缓存统计数据，缓存key=固定前缀 + 缓存名称加 + 内部缓存名
                String redisKey = getRedisStatsKey(cacheName, multiLayerCacheConfig.internalKey());
                CacheStatsInfo cacheStats = cacheManager.getRedissonClient().<CacheStatsInfo>getBucket(redisKey, codec).get();
                if (!Objects.isNull(cacheStats)) {
                    statsList.add(cacheStats);
                }
            }
        }

        return statsList.stream().sorted(Comparator.comparing(CacheStatsInfo::getHitRate)).collect(
                Collectors.toList());
    }

    /**
     * 同步缓存统计list
     */
    public void syncCacheStats() {
        RedissonClient redissonClient = cacheManager.getRedissonClient();
        // 清空统计数据
        resetCacheStat();
        executor.scheduleWithFixedDelay(() -> {
            log.debug("执行缓存统计数据采集定时任务");
            Set<AbstractCacheManager> cacheManagers = AbstractCacheManager.getCacheManager();
            for (AbstractCacheManager abstractCacheManager : cacheManagers) {
                // 获取CacheManager
                CacheManager cacheManager = ((CacheManager) abstractCacheManager);
                Collection<String> cacheNames = cacheManager.getCacheNames();
                for (String cacheName : cacheNames) {
                    // 获取Cache
                    Collection<Cache> caches = cacheManager.getCache(cacheName);
                    for (Cache cache : caches) {
                        MultiLayerCache multiLayerCache = (MultiLayerCache) cache;
                        MultiLayerCacheConfig multiLayerCacheConfig = multiLayerCache.getMultiLayerCacheConfig();
                        // 加锁并增量缓存统计数据，缓存key=固定前缀 +缓存名称加 + 内部缓存名
                        String redisKey = getRedisStatsKey(cacheName, multiLayerCacheConfig.internalKey());
                        RLock lock = RedisLockUtils.getRLock(redissonClient, redisKey);
                        try {
                            if (lock.tryLock()) {
                                CacheStatsInfo cacheStats = redissonClient.<CacheStatsInfo>getBucket(redisKey, codec).get();
                                if (Objects.isNull(cacheStats)) {
                                    cacheStats = new CacheStatsInfo();
                                }

                                // 设置缓存唯一标示
                                cacheStats.setCacheName(cacheName);
                                cacheStats.setInternalKey(multiLayerCacheConfig.internalKey());

                                cacheStats.setDepict(multiLayerCacheConfig.getDepict());
                                // 设置缓存配置信息
                                cacheStats.setMultiLayerCacheConfig(multiLayerCacheConfig);

                                // 设置缓存统计数据
                                CacheStats layeringCacheStats = multiLayerCache.getCacheStats();
                                CacheStats firstCacheStats = multiLayerCache.getFirstCache().getCacheStats();
                                CacheStats secondCacheStats = multiLayerCache.getSecondCache().getCacheStats();

                                // 清空加载缓存时间
                                firstCacheStats.getAndResetCachedMethodRequestTime();
                                secondCacheStats.getAndResetCachedMethodRequestTime();

                                cacheStats.setRequestCount(cacheStats.getRequestCount() + layeringCacheStats.getAndResetCacheRequestCount());
                                cacheStats.setMissCount(cacheStats.getMissCount() + layeringCacheStats.getAndResetCachedMethodRequestCount());
                                cacheStats.setTotalLoadTime(cacheStats.getTotalLoadTime() + layeringCacheStats.getAndResetCachedMethodRequestTime());
                                cacheStats.setHitRate((cacheStats.getRequestCount() - cacheStats.getMissCount()) / (double) cacheStats.getRequestCount() * 100);

                                cacheStats.setFirstCacheRequestCount(cacheStats.getFirstCacheRequestCount() + firstCacheStats.getAndResetCacheRequestCount());
                                cacheStats.setFirstCacheMissCount(cacheStats.getFirstCacheMissCount() + firstCacheStats.getAndResetCachedMethodRequestCount());

                                cacheStats.setSecondCacheRequestCount(cacheStats.getSecondCacheRequestCount() + secondCacheStats.getAndResetCacheRequestCount());
                                cacheStats.setSecondCacheMissCount(cacheStats.getSecondCacheMissCount() + secondCacheStats.getAndResetCachedMethodRequestCount());

                                // 将缓存统计数据写到redis
                                redissonClient.<CacheStatsInfo>getBucket(redisKey, codec).set(cacheStats, 1, TimeUnit.HOURS);

                                log.info("Layering Cache 统计信息：{}", JacksonUtil.toJson(cacheStats));
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }

            //  初始时间间隔是1分
        }, 1, 1, TimeUnit.MINUTES);
    }

    private String getRedisStatsKey(String cacheName, String cacheInnerKey) {
        String redisStatsKey = new StringBuilder(CACHE_STATS_KEY_PREFIX)
                .append(ECacheConstants.REDIS_KEY_SPLIT)
                .append(cacheName)
                .append(ECacheConstants.REDIS_KEY_SPLIT)
                .append(cacheInnerKey)
                .toString();
        return redisStatsKey;
    }

    /**
     * 关闭线程池
     */
    public void shutdownExecutor() {
        executor.shutdown();
    }

    /**
     * 重置缓存统计数据
     */
    public void resetCacheStat() {
        RedissonClient redissonClient = cacheManager.getRedissonClient();
        redissonClient.getKeys().deleteByPattern(CACHE_STATS_KEY_PREFIX + "*");
    }

    public void setCacheManager(AbstractCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
