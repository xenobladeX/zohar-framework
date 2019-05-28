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
package com.xenoblade.zohar.framework.cache.core.config;

import com.xenoblade.zohar.framework.cache.core.support.EEncodeType;
import com.xenoblade.zohar.framework.cache.core.support.EHashType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 二级缓存配置项
 * @author xenoblade
 * @since 1.0.0
 */
@Data
public class SecondaryCacheConfig implements Serializable{

    private static final long serialVersionUID = 1435597244908000981L;

    /**
     * 缓存有效时间
     */
    private long expiration = 5;

    /**
     * 缓存主动在失效前强制刷新缓存的时间
     */
    private long preloadTime = 1;

    /**
     * 时间单位 {@link TimeUnit}
     */
    private TimeUnit timeUnit = TimeUnit.HOURS;

    /**
     * 是否强制刷新（走数据库），默认是false
     */
    private boolean forceRefresh = false;

    /**
     * 是否使用缓存名称作为 redis key 前缀
     */
    private boolean usePrefix = true;

    /**
     * 是否允许存NULL值
     */
    boolean allowNullValue = false;

    /**
     * Key 的编码方式
     */
    private EEncodeType keyEncodeType = EEncodeType.NONE;


    /**
     * Key 的hash 方式
     */
    private EHashType keyHashType = EHashType.NONE;


    /**
     * 非空值和null值之间的时间倍率，默认是1。allowNullValue=true才有效
     * <p>
     * 如配置缓存的有效时间是200秒，倍率设置成10，
     * 那么当缓存value为null时，缓存的有效时间将是20秒，非空时为200秒
     * </p>
     */
    int magnification = 1;

    public SecondaryCacheConfig(long expiration, long preloadTime, TimeUnit timeUnit, boolean forceRefresh,
                                boolean allowNullValues, int magnification) {
        this(expiration, preloadTime, timeUnit, forceRefresh, allowNullValues, magnification, EEncodeType.NONE, EHashType.NONE);
    }

    public SecondaryCacheConfig(long expiration, long preloadTime, TimeUnit timeUnit, boolean forceRefresh,
                                 boolean allowNullValues, int magnification, EEncodeType keyEncodeType, EHashType keyHashType) {
        this.expiration = expiration;
        this.preloadTime = preloadTime;
        this.timeUnit = timeUnit;
        this.forceRefresh = forceRefresh;
        this.allowNullValue = allowNullValues;
        this.magnification = magnification;
        this.usePrefix = true;
        this.keyEncodeType = keyEncodeType;
        this.keyHashType = keyHashType;
    }


    public String internalKey() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("expireTime=").append(timeUnit.toMillis(expiration));
        stringBuilder.append("preloadTime=").append(timeUnit.toMillis(preloadTime));

        return stringBuilder.toString();
    }

}
