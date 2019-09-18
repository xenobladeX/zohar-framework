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
package com.xenoblade.zohar.framework.cache.core.manager;

import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.cache.core.cache.Cache;
import com.xenoblade.zohar.framework.cache.core.cache.MultiLayerCache;
import com.xenoblade.zohar.framework.cache.core.cache.caffeine.CaffeineCache;
import com.xenoblade.zohar.framework.cache.core.cache.redis.RedisCache;
import com.xenoblade.zohar.framework.cache.core.config.FirstCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.MultiLayerCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.SecondaryCacheConfig;
import lombok.Setter;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * MultiLayerCacheManager
 * @author xenoblade
 * @since 1.0.0
 */
public class MultiLayerCacheManager extends AbstractCacheManager{

    @Setter
    private MultiLayerCacheConfig defaultMultiLayerCacheConfig = new MultiLayerCacheConfig();

    /**
     * 缓存名
     */
    @Setter
    private String cacheName;

    public MultiLayerCacheManager(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
        cacheManagers.add(this);
    }

    @Override
    protected Cache getMissingCache(String name, MultiLayerCacheConfig multiLayerCacheConfig) {
        // 创建一级缓存
        CaffeineCache caffeineCache = new CaffeineCache(name, multiLayerCacheConfig.getFirstCacheConfig(), isStats());
        // 创建二级缓存
        // TODO 2 创建 RedisMapCache
        RedisCache redisCache = new RedisCache(name, redissonClient, multiLayerCacheConfig.getSecondaryCacheConfig(), isStats());
        return new MultiLayerCache(redisTemplate, caffeineCache, redisCache, super.isStats(), multiLayerCacheConfig);
    }

    @Override protected void parseMultiLayerCacheConfig(
            String name, MultiLayerCacheConfig multiLayerCacheConfig) {
        if (StrUtil.isBlank(name)) {
            name = cacheName;
        }
        if (multiLayerCacheConfig.getCacheMode() == null) {
            multiLayerCacheConfig.setCacheMode(defaultMultiLayerCacheConfig.getCacheMode());
        }
        if (multiLayerCacheConfig.getFirstCacheConfig() == null) {
            multiLayerCacheConfig.setFirstCacheConfig(defaultMultiLayerCacheConfig.getFirstCacheConfig());
        }
        if (multiLayerCacheConfig.getSecondaryCacheConfig() == null) {
            multiLayerCacheConfig.setSecondaryCacheConfig(defaultMultiLayerCacheConfig.getSecondaryCacheConfig());
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
