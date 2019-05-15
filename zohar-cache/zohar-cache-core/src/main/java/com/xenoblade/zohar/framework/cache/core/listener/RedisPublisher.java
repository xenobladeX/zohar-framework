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

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * RedisPublisher
 * @author xenoblade
 * @since 1.0.0
 */
@UtilityClass
@Slf4j
public class RedisPublisher {

    public static void publisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic, Object message) {
        redisTemplate.convertAndSend(channelTopic.toString(), message);
        log.debug("redis消息发布者向频道【{}】发布了【{}】消息", channelTopic.toString(), message.toString());
    }

}
