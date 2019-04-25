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
package com.xenoblade.zohar.framework.commons.redis.serial;

import com.xenoblade.zohar.framework.commons.redis.support.Type;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * FastJsonRedisSerializer
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> clazz;

    /**
     * 允许所有包的序列化和反序列化，不推荐
     *
     * @param clazz Class
     */
    @Deprecated
    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
        try {
            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        } catch (Throwable e) {
            log.warn("fastjson 版本太低，反序列化有被攻击的风险", e);
        }
        log.warn("fastjson 反序列化有被攻击的风险，推荐使用白名单的方式，详情参考：https://www.jianshu.com/p/a92ecc33fd0d");
    }

    /**
     * 指定小范围包的序列化和反序列化，具体原因可以参考：
     * <P>https://www.jianshu.com/p/a92ecc33fd0d</P>
     *
     * @param clazz    clazz
     * @param packages 白名单包名，如:"com.xxx."
     */
    public FastJsonRedisSerializer(Class<T> clazz, String... packages) {
        super();
        this.clazz = clazz;
        try {
            ParserConfig.getGlobalInstance().addAccept("com.xenoblade.zohar.");
            if (packages != null && packages.length > 0) {
                for (String packageName : packages) {
                    ParserConfig.getGlobalInstance().addAccept(packageName);
                }
            }
        } catch (Throwable e) {
            log.warn("fastjson 版本太低，反序列化有被攻击的风险", e);
        }
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        try {
            return JSON.toJSONString(new FastJsonSerializerWrapper(t), SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new SerializationException(String.format("FastJsonRedisSerializer 序列化异常: %s, 【JSON：%s】",
                    e.getMessage(), JSON.toJSONString(t)), e);

        }

    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (SerializationUtils.isEmpty(bytes)) {
            return null;
        }

        String str = new String(bytes, DEFAULT_CHARSET);
        try {
            FastJsonSerializerWrapper wrapper = JSON.parseObject(str, FastJsonSerializerWrapper.class);

            switch (Type.parse(wrapper.getType())) {
                case STRING:
                    return (T) wrapper.getContent();
                case OBJECT:
                case SET:

                    if (wrapper.getContent() instanceof NullValue) {
                        return null;
                    }

                    return (T) wrapper.getContent();

                case LIST:

                    return (T) ((JSONArray) wrapper.getContent()).toJavaList(clazz);

                case NULL:

                    return null;
                default:
                    throw new SerializationException("不支持反序列化的对象类型: " + wrapper.getType());
            }
        } catch (Exception e) {
            throw new SerializationException(String.format("FastJsonRedisSerializer 反序列化异常: %s, 【JSON：%s】",
                    e.getMessage(), str), e);
        }
    }
}
