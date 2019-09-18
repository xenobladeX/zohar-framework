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

import com.xenoblade.zohar.framework.cache.core.support.ECacheConstants;
import com.xenoblade.zohar.framework.cache.core.support.ECacheMode;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

/**
 * MultiLayerCacheConfig
 * @author xenoblade
 * @since 1.0.0
 */
@Data
public class MultiLayerCacheConfig implements Serializable{

    private static final long serialVersionUID = -7185999794798838069L;

    /**
     * 描述，数据监控页面使用
     */
    private String depict;

    /**
     * 是否使用一级缓存
     */
    private ECacheMode cacheMode = ECacheMode.ALL;

    /**
     * 一级缓存配置
     */
    private FirstCacheConfig firstCacheConfig = new FirstCacheConfig();

    /**
     * 二级缓存配置
     */
    private SecondaryCacheConfig secondaryCacheConfig = new SecondaryCacheConfig();

    public MultiLayerCacheConfig() {
    }

    public MultiLayerCacheConfig(FirstCacheConfig firstCacheConfig, SecondaryCacheConfig secondaryCacheConfig,
                                String depict) {
        this.firstCacheConfig = firstCacheConfig;
        this.secondaryCacheConfig = secondaryCacheConfig;
        this.depict = depict;
    }


    public String internalKey() {
        // 一级缓存有效时间-二级缓存有效时间-二级缓存自动刷新时间
        StringBuilder sb = new StringBuilder();
        if (firstCacheConfig != null) {
            sb.append(firstCacheConfig.internalKey());
        }
        sb.append(ECacheConstants.REDIS_KEY_INNER_SPLIT);
        if (secondaryCacheConfig != null) {
            sb.append(secondaryCacheConfig.internalKey());
        }
        return sb.toString();
    }


}
