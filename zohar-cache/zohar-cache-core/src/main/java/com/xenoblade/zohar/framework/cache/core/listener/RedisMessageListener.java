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
package com.xenoblade.zohar.framework.cache.core.listener;

import com.xenoblade.zohar.framework.cache.core.cache.Cache;
import com.xenoblade.zohar.framework.cache.core.cache.MultiLayerCache;
import com.xenoblade.zohar.framework.cache.core.manager.AbstractCacheManager;
import com.xenoblade.zohar.framework.commons.utils.jackson.JacksonUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.Collection;

/**
 * RedisMessageListener
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
@Setter
public class RedisMessageListener extends MessageListenerAdapter{

    /**
     * 缓存管理器
     */
    private AbstractCacheManager cacheManager;

    @Override public void onMessage(Message message, byte[] pattern) {
        super.onMessage(message, pattern);
        // 解析订阅发布的信息，获取缓存的名称和缓存的key
        RedisPubSubMessage redisPubSubMessage = (RedisPubSubMessage) cacheManager.getRedisTemplate()
                .getValueSerializer().deserialize(message.getBody());
        log.debug("redis消息订阅者接收到频道【{}】发布的消息。消息内容：{}", new String(message.getChannel()), JacksonUtil.toJson(redisPubSubMessage));

        // 根据缓存名称获取多级缓存，可能有多个
        Collection<Cache> caches = cacheManager.getCache(redisPubSubMessage.getCacheName());
        for (Cache cache : caches) {
            // 判断缓存是否是多级缓存
            if (cache != null && cache instanceof MultiLayerCache) {
                switch (redisPubSubMessage.getMessageType()) {
                    case EVICT:
                        // 获取一级缓存，并删除一级缓存数据
                        ((MultiLayerCache) cache).getFirstCache().evict(redisPubSubMessage.getKey());
                        log.info("删除一级缓存{}数据,key={}", redisPubSubMessage.getCacheName(), redisPubSubMessage.getKey());
                        break;

                    case CLEAR:
                        // 获取一级缓存，并删除一级缓存数据
                        ((MultiLayerCache) cache).getFirstCache().clear();
                        log.info("清除一级缓存{}数据", redisPubSubMessage.getCacheName());
                        break;

                    default:
                        log.error("接收到没有定义的订阅消息频道数据");
                        break;
                }

            }
        }
    }
}
