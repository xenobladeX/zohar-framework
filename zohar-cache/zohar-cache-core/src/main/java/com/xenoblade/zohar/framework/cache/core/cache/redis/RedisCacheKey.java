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
package com.xenoblade.zohar.framework.cache.core.cache.redis;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HashUtil;
import com.xenoblade.zohar.framework.cache.core.support.ECacheConstants;
import com.xenoblade.zohar.framework.commons.redis.serial.key.DefaultStringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * RedisCacheKey
 * @author xenoblade
 * @since 1.0.0
 */
public class RedisCacheKey {

    /**
     * 前缀序列化器
     */
    private final RedisSerializer prefixSerializer = new DefaultStringRedisSerializer();

    /**
     * 缓存key
     */
    private final Object keyElement;

    /**
     * 缓存名称
     */
    private String cacheName;

    /**
     * 是否使用缓存前缀
     */
    private boolean usePrefix = true;

    // TODO 是否使用base64编码序列化的结果
    // TODO 使用何种 Hash 方式
    private boolean useBase64 = true;

    /**
     * RedisTemplate 的key序列化器
     */
    private final RedisSerializer serializer;

    /**
     * @param keyElement 缓存key
     * @param serializer RedisSerializer
     */
    public RedisCacheKey(Object keyElement, RedisSerializer serializer) {

        Assert.notNull(keyElement, "缓存key不能为NULL");
        Assert.notNull(serializer, "key的序列化器不能为NULL");
        this.keyElement = keyElement;
        this.serializer = serializer;
    }

    /**
     * 获取缓存key
     *
     * @return String
     */
    public String getKey() {

        return new String(getKeyBytes());
    }

    /**
     * 获取key的byte数组
     *
     * @return byte[]
     */
    public byte[] getKeyBytes() {

        byte[] rawKey = serializeKeyElement();
        if (!usePrefix) {
            return rawKey;
        }

        byte[] prefix = getPrefix();
        byte[] prefixedKey = Arrays.copyOf(prefix, prefix.length + rawKey.length);
        System.arraycopy(rawKey, 0, prefixedKey, prefix.length, rawKey.length);

        return prefixedKey;
    }

    private byte[] serializeKeyElement() {

        if (serializer == null && keyElement instanceof byte[]) {
            return (byte[]) keyElement;
        }

        byte[] bytes = serializer.serialize(keyElement);

        // 对keyElement的序列化字符串进行Base64编码
        if (!(keyElement instanceof String) && useBase64) {
            return Base64.encode(bytes, false);
        }
        return bytes;
    }

    /**
     * 获取缓存前缀，默认缓存前缀是":"
     *
     * @return byte[]
     */
    public byte[] getPrefix() {
        return prefixSerializer.serialize((StringUtils.isEmpty(cacheName) ? cacheName.concat(
                ECacheConstants.REDIS_KEY_SPLIT) : cacheName.concat(ECacheConstants.REDIS_KEY_SPLIT)));
    }

    /**
     * 设置缓存名称
     *
     * @param cacheName cacheName
     * @return RedisCacheKey
     */
    public RedisCacheKey cacheName(String cacheName) {
        this.cacheName = cacheName;
        return this;
    }

    /**
     * 设置是否使用缓存前缀，默认使用
     *
     * @param usePrefix usePrefix
     * @return RedisCacheKey
     */
    public RedisCacheKey usePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
        return this;
    }

}
