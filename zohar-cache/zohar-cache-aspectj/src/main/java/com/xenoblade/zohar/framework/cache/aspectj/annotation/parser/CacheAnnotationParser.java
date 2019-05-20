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
package com.xenoblade.zohar.framework.cache.aspectj.annotation.parser;

import com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheEvict;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CachePut;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.Cacheable;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CacheEvictOperation;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CachePutOperation;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CacheableOperation;

import java.lang.reflect.Method;

/**
 * CacheAnnotationParser
 * @author xenoblade
 * @since 1.0.0
 */
public interface CacheAnnotationParser {

    /**
     * 处理{@link Cacheable} 注解
     * @param method
     * @return
     */
    CacheableOperation parseCacheable(Method method);

    /**
     * 处理{@link CachePut} 注解
     * @param method
     * @return
     */
    CachePutOperation parseCachePut(Method method);

    /**
     * 处理{@link CacheEvict} 注解
     * @param method
     * @return
     */
    CacheEvictOperation parseCacheEvict(Method method);


}
