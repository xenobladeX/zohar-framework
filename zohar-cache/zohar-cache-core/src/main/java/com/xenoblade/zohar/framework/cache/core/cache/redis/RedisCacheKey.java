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
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;
import com.xenoblade.zohar.framework.cache.core.support.ECacheConstants;
import com.xenoblade.zohar.framework.cache.core.support.EEncodeType;
import com.xenoblade.zohar.framework.cache.core.support.EHashType;
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

    /**
     * 编码类型
     */
    private EEncodeType encodeType;

    /**
     * 哈希类型
     */
    private EHashType hashType;

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
        if (EHashType.NONE == hashType) {
            // 如果不做哈希，则使用编码方法
            rawKey = encode(rawKey, encodeType);
        } else {
            // 直接进行哈希算法
            rawKey = hash(rawKey, hashType);
        }

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

        return bytes;
    }

    private byte[] encode(byte[] bytes, EEncodeType encodeType) {
        if (bytes.length == 0) {
            return new byte[0];
        }
        byte[] encodeBytes = Arrays.copyOf(bytes, bytes.length);
        switch (encodeType) {
            case BASE64:
            {
                encodeBytes = Base64.encode(bytes, false);
                break;
            }
            case HEX:
            {
                String encodeStr = HexUtil.encodeHexStr(bytes);
                encodeBytes = encodeStr.getBytes();
                break;
            }
        }
        return encodeBytes;
    }

    private byte[] hash(byte[] bytes, EHashType hashType) {
        byte[] hashBytes = Arrays.copyOf(bytes, bytes.length);
        switch (hashType) {
            case MD5: {
                // md5 + hex
                byte[] hash = SecureUtil.md5().digest(bytes);
                hashBytes = encode(hash, EEncodeType.HEX);
                break;
            }
            case SHA1: {
                // sha-1 + hex
                byte[] hash = SecureUtil.sha1().digest(bytes);
                hashBytes = encode(hash, EEncodeType.HEX);
                break;
            }
            case HMAC_SHA1: {
                // hmac + sha1
                byte[] hash = SecureUtil.hmacSha1().digest(bytes);
                hashBytes = encode(hash, EEncodeType.HEX);
                break;
            }
            case HMAC_MD5: {
                // hmac + sha1
                byte[] hash = SecureUtil.hmacMd5().digest(bytes);
                hashBytes = encode(hash, EEncodeType.HEX);
                break;
            }

        }
        return hashBytes;
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

    /**
     * 设置编码类型
     * @param encodeType
     * @return
     */
    public RedisCacheKey encodeType(EEncodeType encodeType) {
        this.encodeType = encodeType;
        return this;
    }

    /**
     * 设置哈希类型
     * @param hashType
     * @return
     */
    public RedisCacheKey hashType(EHashType hashType) {
        this.hashType = hashType;
        return this;
    }

}
