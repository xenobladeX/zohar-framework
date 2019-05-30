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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.commons.redis.serial.SerializationUtils;
import com.xenoblade.zohar.framework.commons.redis.serial.key.DefaultStringRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.key.FastJsonStringRedisSerilizer;
import com.xenoblade.zohar.framework.commons.redis.serial.key.JacksonStringRedisSerilaizer;
import com.xenoblade.zohar.framework.commons.redis.serial.key.JdkSerializationStringRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.key.KryoStringRedisSerilaizer;
import com.xenoblade.zohar.framework.commons.redis.serial.value.FastJsonRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.value.KryoRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.support.InvalidRedisSerializerException;
import com.xenoblade.zohar.framework.commons.utils.support.EEncodeType;
import com.xenoblade.zohar.framework.commons.utils.support.EHashType;
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
        switch (redisProperties.getRedisTemplate().getValueSerial()) {

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
                throw new InvalidRedisSerializerException(StrUtil.format("Invalid reids object serializer: {}", redisProperties.getRedisTemplate().getValueSerial()));
            }
        }

        // 设置键（key）的序列化采用支持 Object 的StringRedisSerializer。
        redisTemplate.setKeySerializer(new DefaultStringRedisSerializer());
        redisTemplate.setHashKeySerializer(new DefaultStringRedisSerializer());

        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = "objectRedisTemplate")
    public RedisTemplate<Object, Object> objectRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> objectRedisTemplate = new RedisTemplate<Object, Object>();
        objectRedisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置值（value）的序列化
        switch (redisProperties.getObjectRedisTemplate().getValueSerial()) {

            case JDK:
            {
                objectRedisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
                objectRedisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
                break;
            }
            case KRYO:
            {
                objectRedisTemplate.setValueSerializer(new KryoRedisSerializer(Object.class));
                objectRedisTemplate.setHashValueSerializer(new KryoRedisSerializer(Object.class));
                break;
            }
            case JACKSON:
            {
                ObjectMapper jsonRedisObjectMapper = SerializationUtils.jsonRedisObjectMapper();
                objectRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(jsonRedisObjectMapper));
                objectRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(jsonRedisObjectMapper));
                break;
            }
            case FASTJSON:
            {
                objectRedisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class, null));
                objectRedisTemplate.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class, null));
                break;
            }
            default:
            {
                throw new InvalidRedisSerializerException(StrUtil.format("Invalid reids object serializer: {}", redisProperties.getObjectRedisTemplate().getValueSerial()));
            }
        }

        // 设置键（key）的序列化采用支持 Object 的StringRedisSerializer。
        EHashType hashType = redisProperties.getObjectRedisTemplate().getHashType();
        EEncodeType encodeType =  redisProperties.getObjectRedisTemplate().getEncodeType();
        switch (redisProperties.getObjectRedisTemplate().getKeySerial()) {
            case STRING:
            {
                objectRedisTemplate.setKeySerializer(new DefaultStringRedisSerializer(encodeType, hashType));
                objectRedisTemplate.setHashKeySerializer(new DefaultStringRedisSerializer(encodeType, hashType));
                break;
            }
            case FASTJSON:
            {
                objectRedisTemplate.setKeySerializer(new FastJsonStringRedisSerilizer(encodeType, hashType));
                objectRedisTemplate.setHashKeySerializer(new FastJsonStringRedisSerilizer(encodeType, hashType));
                break;
            }
            case JACKSON:
            {
                objectRedisTemplate.setKeySerializer(new JacksonStringRedisSerilaizer(encodeType, hashType));
                objectRedisTemplate.setHashKeySerializer(new JacksonStringRedisSerilaizer(encodeType, hashType));
                break;
            }
            case KRYO:
            {
                objectRedisTemplate.setKeySerializer(new KryoStringRedisSerilaizer(encodeType, hashType));
                objectRedisTemplate.setHashKeySerializer(new KryoStringRedisSerilaizer(encodeType, hashType));
                break;
            }
            case JDK:
            {
                objectRedisTemplate.setKeySerializer(new JdkSerializationStringRedisSerializer(encodeType, hashType));
                objectRedisTemplate.setHashKeySerializer(new JdkSerializationStringRedisSerializer(encodeType, hashType));
                break;
            }
            default:
            {
                throw new InvalidRedisSerializerException(StrUtil.format("Invalid reids key serializer: {}", redisProperties.getObjectRedisTemplate().getKeySerial()));
            }
        }

        return objectRedisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置值（value）的序列化
        stringRedisTemplate.setValueSerializer(new DefaultStringRedisSerializer());
        stringRedisTemplate.setHashValueSerializer(new DefaultStringRedisSerializer());

        // 设置键（key）的序列化采用支持 Object 的StringRedisSerializer。
        stringRedisTemplate.setKeySerializer(new DefaultStringRedisSerializer());
        stringRedisTemplate.setHashKeySerializer(new DefaultStringRedisSerializer());

        return stringRedisTemplate;
    }


}
