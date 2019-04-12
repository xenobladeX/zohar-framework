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
package com.xenoblade.zohar.framework.redis.starter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xenoblade.zohar.framework.commons.utils.jackson.JacksonUtil;
import com.xenoblade.zohar.framework.commons.utils.jackson.protobuf.CustomProtobufModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

/**
 * RedisTemplateConfig
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
public class RedisTemplateConfig {

    // TODO: 增加 fastjson 等序列化方式
    @Configuration
    @ConditionalOnProperty(prefix = "zohar.redis.template", name = "serial", havingValue = "json", matchIfMissing = true)
    public static class JsonRedisTemplateConfig {

        @Bean
        public ObjectMapper jsonRedisObjectMapper() {
            ObjectMapper jsonRedisObjectMapper = new ObjectMapper();
            jsonRedisObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            jsonRedisObjectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            // 序列化异常不抛出
            jsonRedisObjectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            jsonRedisObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            // 忽略不能转移的字符
            jsonRedisObjectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            // 自定义序列化方式
            SimpleModule simpleModule = new SimpleModule();
            // Long 转 String 防止精度丢失
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            jsonRedisObjectMapper.registerModule(simpleModule);
            // Jackson protobuf format
            jsonRedisObjectMapper.registerModule(new CustomProtobufModule());


            return jsonRedisObjectMapper;
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, @Qualifier("jsonRedisObjectMapper") ObjectMapper jsonRedisObjectMapper) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);

            Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);

            jackson2JsonRedisSerializer.setObjectMapper(jsonRedisObjectMapper);

            // 设置值（value）的序列化采用Jackson2JsonRedisSerializer。
            redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
            // 设置键（key）的序列化采用StringRedisSerializer。
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());

            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "zohar.redis.template", name = "serial", havingValue = "jdk")
    public static class JdkRedisTemplateConfig {

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);

            // 设置值（value）的序列化采用JdkSerializationRedisSerializer
            redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
            redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
            // 设置键（key）的序列化采用StringRedisSerializer。
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());

            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        }

    }


}
