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
package com.xenoblade.zohar.framework.cache.aspectj.config;

import com.xenoblade.zohar.framework.cache.aspectj.MultiLayerAspect;
import com.xenoblade.zohar.framework.cache.core.config.FirstCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.MultiLayerCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.SecondaryCacheConfig;
import com.xenoblade.zohar.framework.cache.core.manager.CacheManager;
import com.xenoblade.zohar.framework.cache.core.manager.MultiLayerCacheManager;
import com.xenoblade.zohar.framework.cache.core.support.ECacheMode;
import com.xenoblade.zohar.framework.commons.redis.serial.ERedisSerialType;
import com.xenoblade.zohar.framework.commons.utils.support.EEncodeType;
import com.xenoblade.zohar.framework.cache.core.support.EExpireMode;
import com.xenoblade.zohar.framework.commons.utils.support.EHashType;
import com.xenoblade.zohar.framework.redis.starter.RedissonAutoConfiguration;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;

/**
 * CacheConfig
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
@Import({RedissonAutoConfiguration.class})
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        MultiLayerCacheManager multiLayerCacheManager = new MultiLayerCacheManager(redissonClient);
        // 开启统计功能
        multiLayerCacheManager.setStats(true);
        multiLayerCacheManager.setCacheName("zohar-cache");

        // set defaultMultiLayerCacheConfig
        MultiLayerCacheConfig defaultMultiLayerCacheConfig = new MultiLayerCacheConfig();
        defaultMultiLayerCacheConfig.setCacheMode(ECacheMode.ALL);
        // firstCacheConfig
        FirstCacheConfig firstCacheConfig = new FirstCacheConfig(10,
                500, 9, TimeUnit.SECONDS, EExpireMode.WRITE);
        defaultMultiLayerCacheConfig.setFirstCacheConfig(firstCacheConfig);
        // secondaryCacheConfig
        SecondaryCacheConfig secondaryCacheConfig = new SecondaryCacheConfig(10,
                1, TimeUnit.SECONDS, false, false, 1, ERedisSerialType.JDK,
                EEncodeType.NONE, EHashType.MD5, ERedisSerialType.JACKSON);
        defaultMultiLayerCacheConfig.setSecondaryCacheConfig(secondaryCacheConfig);

        multiLayerCacheManager.setDefaultMultiLayerCacheConfig(defaultMultiLayerCacheConfig);
        return multiLayerCacheManager;
    }

    @Bean
    public MultiLayerAspect multiLayerAspect(CacheManager cacheManager) {
        return new MultiLayerAspect(cacheManager);
    }

}
