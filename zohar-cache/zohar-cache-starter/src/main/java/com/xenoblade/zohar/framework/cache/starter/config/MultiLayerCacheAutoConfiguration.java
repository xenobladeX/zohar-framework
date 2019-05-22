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
package com.xenoblade.zohar.framework.cache.starter.config;

import com.xenoblade.zohar.framework.cache.aspectj.MultiLayerAspect;
import com.xenoblade.zohar.framework.cache.core.config.FirstCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.MultiLayerCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.SecondaryCacheConfig;
import com.xenoblade.zohar.framework.cache.core.manager.CacheManager;
import com.xenoblade.zohar.framework.cache.core.manager.MultiLayerCacheManager;
import com.xenoblade.zohar.framework.cache.starter.property.FirstCacheProperties;
import com.xenoblade.zohar.framework.cache.starter.property.MultiLayerCacheProperties;
import com.xenoblade.zohar.framework.cache.starter.property.SecnodaryCacheProperties;
import com.xenoblade.zohar.framework.redis.starter.RedisTemplateConfiguration;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * MultiLayerCacheAutoConfiguration
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
@ConditionalOnBean( {RedisTemplate.class, RedissonClient.class} )
@AutoConfigureAfter({RedisTemplateConfiguration.class})
@EnableAspectJAutoProxy
@EnableConfigurationProperties({MultiLayerCacheProperties.class})
public class MultiLayerCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient,
                                     MultiLayerCacheProperties multiLayerCacheProperties) {
        MultiLayerCacheManager multiLayerCacheManager = new MultiLayerCacheManager(redisTemplate, redissonClient);
        // 开启统计功能
        multiLayerCacheManager.setStats(multiLayerCacheProperties.getStats());
        multiLayerCacheManager.setCacheName(multiLayerCacheProperties.getCacheName());

        // set defaultMultiLayerCacheConfig
        MultiLayerCacheConfig defaultMultiLayerCacheConfig = new MultiLayerCacheConfig();
        defaultMultiLayerCacheConfig.setCacheMode(multiLayerCacheProperties.getCacheMode());
        // firstCacheConfig
        FirstCacheProperties firstCacheProperties = multiLayerCacheProperties.getFirstCache();
        FirstCacheConfig firstCacheConfig = new FirstCacheConfig(firstCacheProperties.getInitialCapacity(),
                firstCacheProperties.getMaximumSize(), firstCacheProperties.getExpireTime(), firstCacheProperties.getTimeUnit(),
                firstCacheProperties.getExpireMode());
        defaultMultiLayerCacheConfig.setFirstCacheConfig(firstCacheConfig);
        // secondaryCacheConfig
        SecnodaryCacheProperties secnodaryCacheProperties = multiLayerCacheProperties.getSecnodaryCache();
        SecondaryCacheConfig secondaryCacheConfig = new SecondaryCacheConfig(secnodaryCacheProperties.getExpiration(),
                secnodaryCacheProperties.getPreloadTime(), secnodaryCacheProperties.getTimeUnit(), secnodaryCacheProperties.isForceRefresh(),
                secnodaryCacheProperties.isAllowNullValue(), secnodaryCacheProperties.getMagnification(),
                secnodaryCacheProperties.getKeyEncodeType(), secnodaryCacheProperties.getKeyHashType());
        defaultMultiLayerCacheConfig.setSecondaryCacheConfig(secondaryCacheConfig);

        multiLayerCacheManager.setDefaultMultiLayerCacheConfig(defaultMultiLayerCacheConfig);
        return multiLayerCacheManager;
    }

    @Bean
    public MultiLayerAspect multiLayerAspect(CacheManager cacheManager) {
        return new MultiLayerAspect(cacheManager);
    }


}
