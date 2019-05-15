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

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.SerializationUtils;
import com.xenoblade.zohar.framework.commons.redis.serial.key.AbstractStringRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.key.DefaultStringRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.key.FastJsonStringRedisSerilizer;
import com.xenoblade.zohar.framework.commons.redis.serial.key.JacksonStringRedisSerilaizer;
import com.xenoblade.zohar.framework.commons.redis.serial.key.JdkSerializationStringRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.key.KryoStringRedisSerilaizer;
import com.xenoblade.zohar.framework.commons.redis.serial.value.FastJsonRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.value.KryoRedisSerializer;
import com.xenoblade.zohar.framework.commons.utils.jackson.protobuf.CustomProtobufModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;


/**
 * RedisTemplateConfig
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(RedissonAutoConfiguration.class)
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@EnableConfigurationProperties({RedisProperties.class})
public class RedisTemplateConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置值（value）的序列化
        switch (redisProperties.getObjctTemplate().getValueSerial()) {

            case JDK:
            {
                redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
                redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
                break;
            }
            case KRYO:
            {
                redisTemplate.setValueSerializer(new KryoRedisSerializer(Object.class));
                redisTemplate.setHashValueSerializer(new KryoRedisSerializer(Object.class));
                break;
            }
            case JACKSON:
            {
                ObjectMapper jsonRedisObjectMapper = SerializationUtils.jsonRedisObjectMapper();
                redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(jsonRedisObjectMapper));
                redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(jsonRedisObjectMapper));
                break;
            }
            case FASTJSON:
            {
                redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class, null));
                redisTemplate.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class, null));
                break;
            }
            default:
            {
                throw new InvalidRedisSerializerException(StrUtil.format("Invalid reids object serializer: {}", redisProperties.getObjctTemplate().getValueSerial()));
            }
        }

        // 设置键（key）的序列化采用支持 Object 的StringRedisSerializer。
        String keyPrefix = redisProperties.getObjctTemplate().getKeyPrefix();
        switch (redisProperties.getObjctTemplate().getKeySerial()) {
            case STRING:
            {
                redisTemplate.setKeySerializer(new DefaultStringRedisSerializer(keyPrefix));
                redisTemplate.setHashKeySerializer(new DefaultStringRedisSerializer(keyPrefix));
                break;
            }
            case FASTJSON:
            {
                redisTemplate.setKeySerializer(new FastJsonStringRedisSerilizer(keyPrefix));
                redisTemplate.setHashKeySerializer(new FastJsonStringRedisSerilizer(keyPrefix));
                break;
            }
            case JACKSON:
            {
                redisTemplate.setKeySerializer(new JacksonStringRedisSerilaizer(keyPrefix));
                redisTemplate.setHashKeySerializer(new JacksonStringRedisSerilaizer(keyPrefix));
                break;
            }
            case KRYO:
            {
                redisTemplate.setKeySerializer(new KryoStringRedisSerilaizer(keyPrefix));
                redisTemplate.setHashKeySerializer(new KryoStringRedisSerilaizer(keyPrefix));
                break;
            }
            case JDK:
            {
                redisTemplate.setKeySerializer(new JdkSerializationStringRedisSerializer(keyPrefix));
                redisTemplate.setHashKeySerializer(new JdkSerializationStringRedisSerializer(keyPrefix));
                break;
            }
            default:
            {
                throw new InvalidRedisSerializerException(StrUtil.format("Invalid reids key serializer: {}", redisProperties.getObjctTemplate().getKeySerial()));
            }
        }

        return redisTemplate;
    }

    // TODO: stringRedisTemplate 不需要其他序列化方式
    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置值（value）的序列化
        switch (redisProperties.getStringTemplate().getValueSerial()) {
            case STRING: {
                stringRedisTemplate.setValueSerializer(new DefaultStringRedisSerializer());
                stringRedisTemplate.setHashValueSerializer(new DefaultStringRedisSerializer());
                break;
            }
            case FASTJSON: {
                stringRedisTemplate.setValueSerializer(new FastJsonStringRedisSerilizer());
                stringRedisTemplate.setHashValueSerializer(new FastJsonStringRedisSerilizer());
                break;
            }
            case JACKSON: {
                stringRedisTemplate.setValueSerializer(new JacksonStringRedisSerilaizer());
                stringRedisTemplate.setHashValueSerializer(new JacksonStringRedisSerilaizer());
                break;
            }
            case KRYO: {
                stringRedisTemplate.setValueSerializer(new KryoStringRedisSerilaizer());
                stringRedisTemplate.setHashValueSerializer(new KryoStringRedisSerilaizer());
                break;
            }
            case JDK: {
                stringRedisTemplate.setValueSerializer(new JdkSerializationStringRedisSerializer());
                stringRedisTemplate.setHashValueSerializer(new JdkSerializationStringRedisSerializer());
                break;
            }
            default: {
                throw new InvalidRedisSerializerException(StrUtil.format("Invalid reids value serializer: {}",
                        redisProperties.getObjctTemplate().getValueSerial()));
            }
        }

        // 设置键（key）的序列化采用支持 Object 的StringRedisSerializer。
        String keyPrefix = redisProperties.getStringTemplate().getKeyPrefix();
        switch (redisProperties.getStringTemplate().getKeySerial()) {
            case STRING: {
                stringRedisTemplate.setKeySerializer(new DefaultStringRedisSerializer(keyPrefix));
                stringRedisTemplate.setHashKeySerializer(new DefaultStringRedisSerializer(keyPrefix));
                break;
            }
            case FASTJSON: {
                stringRedisTemplate.setKeySerializer(new FastJsonStringRedisSerilizer(keyPrefix));
                stringRedisTemplate.setHashKeySerializer(new FastJsonStringRedisSerilizer(keyPrefix));
                break;
            }
            case JACKSON: {
                stringRedisTemplate.setKeySerializer(new JacksonStringRedisSerilaizer(keyPrefix));
                stringRedisTemplate.setHashKeySerializer(new JacksonStringRedisSerilaizer(keyPrefix));
                break;
            }
            case KRYO: {
                stringRedisTemplate.setKeySerializer(new KryoStringRedisSerilaizer(keyPrefix));
                stringRedisTemplate.setHashKeySerializer(new KryoStringRedisSerilaizer(keyPrefix));
                break;
            }
            case JDK: {
                stringRedisTemplate.setKeySerializer(new JdkSerializationStringRedisSerializer(keyPrefix));
                stringRedisTemplate.setHashKeySerializer(new JdkSerializationStringRedisSerializer(keyPrefix));
                break;
            }
            default: {
                throw new InvalidRedisSerializerException(StrUtil.format("Invalid reids key serializer: {}",
                        redisProperties.getObjctTemplate().getKeySerial()));
            }
        }
        return stringRedisTemplate;
    }


}
