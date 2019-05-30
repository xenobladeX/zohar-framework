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

import com.xenoblade.zohar.framework.cache.core.support.ECacheMode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

/**
 * MultiLayerCacheProperties
 * @author xenoblade
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "zohar.multi-layer-cache")
@Data
public class MultiLayerCacheProperties implements Serializable{

    private static final long serialVersionUID = -1851585837066249652L;
    /**
     * 是否开启缓存统计（全局）
     */
    private Boolean stats = true;

    /**
     * cache 模式
     */
    private ECacheMode cacheMode = ECacheMode.ALL;

    /**
     * 缓存名
     */
    private String cacheName = "zohar-cache";

    /**
     * 一级缓存配置
     */
    @NestedConfigurationProperty
    private FirstCacheProperties firstCache = new FirstCacheProperties();

    /**
     * 二级缓存配置
     */
    @NestedConfigurationProperty
    private SecnodaryCacheProperties secnodaryCache = new SecnodaryCacheProperties();

}
