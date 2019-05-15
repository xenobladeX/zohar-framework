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

import com.xenoblade.zohar.framework.cache.core.support.EExpireMode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 一级缓存配置项
 * @author xenoblade
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class FirstCacheConfig implements Serializable{

    private static final long serialVersionUID = -798857359716343190L;
    /**
     * 缓存初始Size
     */
    private int initialCapacity = 10;

    /**
     * 缓存最大Size
     */
    private int maximumSize = 500;

    /**
     * 缓存有效时间
     */
    private int expireTime = 0;

    /**
     * 缓存时间单位
     */
    // TODO: use Duration
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * 缓存失效模式{@link EExpireMode}
     */
    private EExpireMode expireMode = EExpireMode.WRITE;

}
