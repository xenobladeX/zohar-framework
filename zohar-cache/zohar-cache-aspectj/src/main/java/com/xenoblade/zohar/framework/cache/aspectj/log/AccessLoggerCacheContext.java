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
package com.xenoblade.zohar.framework.cache.aspectj.log;

import com.xenoblade.zohar.framework.cache.core.config.MultiLayerCacheConfig;
import com.xenoblade.zohar.framework.commons.log.api.AccessLoggerContext;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * AccessLoggerCacheContext
 * @author xenoblade
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class AccessLoggerCacheContext implements AccessLoggerContext {

    private static final long serialVersionUID = 5330607456141748587L;

    /**
     * 是否被触发
     */
    private Boolean isTriggered = false;

    private ECacheOperationType operationType;

    private Object key;

    private String name;

    private MultiLayerCacheConfig multiLayerCacheConfig;


    @Override public String contextType() {
        return "ZOHAR-CACHE";
    }
}
