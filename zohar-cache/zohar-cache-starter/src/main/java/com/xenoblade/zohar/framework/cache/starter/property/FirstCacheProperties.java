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
package com.xenoblade.zohar.framework.cache.starter.property;

import com.xenoblade.zohar.framework.cache.core.support.EExpireMode;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * FirstCacheProperties
 * @author xenoblade
 * @since 1.0.0
 */
@Data
public class FirstCacheProperties implements Serializable {

    private static final long serialVersionUID = 3590707686623423702L;
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
    private int expireTime = 9;

    /**
     * 缓存时间单位
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * 缓存失效模式{@link EExpireMode}
     */
    private EExpireMode expireMode = EExpireMode.WRITE;

}
