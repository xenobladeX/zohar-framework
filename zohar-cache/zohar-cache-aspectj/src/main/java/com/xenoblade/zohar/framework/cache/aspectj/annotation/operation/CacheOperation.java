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
package com.xenoblade.zohar.framework.cache.aspectj.annotation.operation;

import com.xenoblade.zohar.framework.cache.aspectj.annotation.AnnotationConstants;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.FirstCache;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.SecondaryCache;
import com.xenoblade.zohar.framework.cache.core.manager.CacheManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * CacheOperation
 * @author xenoblade
 * @since 1.0.0
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public abstract class CacheOperation implements Serializable{

    private static final long serialVersionUID = -5661591474556579015L;

    /**
     * Names of the default caches to consider for caching operations defined
     * in the annotated class.
     * <p>If none is set at the operation level, these are used instead of the default.
     * <p>May be used to determine the target cache (or caches), matching the
     * qualifier value or the bean names of a specific bean definition.
     */
    private String[] cacheNames = {};

    /**
     * The bean name of the default {@link org.springframework.cache.interceptor.KeyGenerator} to
     * use for the class.
     * <p>If none is set at the operation level, this one is used instead of the default.
     * <p>The key generator is mutually exclusive with the use of a custom key. When such key is
     * defined for the operation, the value of this key generator is ignored.
     */
    private String keyGenerator = "";

    /**
     * The bean name of the custom {@link CacheManager}
     */
    private String cacheManager = "";


    /**
     * 缓存key，支持SpEL表达式
     */
    private String key;

    /**
     * 是否忽略在操作缓存中遇到的异常，如反序列化异常，默认true。
     * <p>true: 有异常会输出warn级别的日志，并直接执行被缓存的方法（缓存将失效）</p>
     * <p>false:有异常会输出error级别的日志，并抛出异常</p>
     *
     */
    private boolean ignoreException = true;

    /**
     * 一级缓存配置
     *
     */
    private FirstCache firstCache = AnnotationConstants.defaultFirstCache();

    /**
     * 二级缓存配置
     *
     * @return SecondaryCache
     */
    private SecondaryCache secondaryCache = AnnotationConstants.defaultSecondaryCache();


}
