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
import com.xenoblade.zohar.framework.commons.redis.serial.FastJsonRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.KryoRedisSerializer;
import com.xenoblade.zohar.framework.commons.redis.serial.StringRedisSerializer;
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
public class RedisTemplateConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置值（value）的序列化
        switch (redisProperties.getTemplate().getSerial()) {

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
                ObjectMapper jsonRedisObjectMapper = jsonRedisObjectMapper();
                redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(jsonRedisObjectMapper));
                redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(jsonRedisObjectMapper));
                break;
            }
            case FASTJSON:
            {
                redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class, "com.xenoblade.zohar."));
                redisTemplate.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class, "com.xenoblade.zohar."));
                break;
            }
            default:
            {
                throw new InvalidRedisSerializerException(StrUtil.format("Invalid reids serializer: {}", redisProperties.getTemplate().getSerial()));
            }
        }

        // 设置键（key）的序列化采用支持 Object 的StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置值（value）的序列化
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // 设置键（key）的序列化采用支持 Object 的StringRedisSerializer。
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());

        return stringRedisTemplate;
    }


    private ObjectMapper jsonRedisObjectMapper() {
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

}
