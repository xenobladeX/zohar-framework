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

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.AnnotationConstants;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheConfig;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheEvict;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CachePut;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.Cacheable;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CacheEvictOperation;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CacheOperation;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CachePutOperation;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CacheableOperation;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * DefaultCacheAnnotationParser
 * @author xenoblade
 * @since 1.0.0
 */
public class DefaultCacheAnnotationParser implements CacheAnnotationParser {

    /**
     * 处理{@link Cacheable} 注解
     * @param method
     * @return
     */
    @Override public CacheableOperation parseCacheable(Method method) {
        Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
        if (cacheable == null) {
            return null;
        }
        CacheableOperation cacheableOperation = new CacheableOperation();
        cacheableOperation.setCacheNames(cacheable.cacheNames());
        cacheableOperation.setDepict(cacheable.depict());
        cacheableOperation.setKey(cacheable.key());
        cacheableOperation.setCondition(cacheable.condition());
        cacheableOperation.setIgnoreException(cacheable.ignoreException());
        if (cacheable.firstCache().length > 0) {
            cacheableOperation.setFirstCache(cacheable.firstCache()[0]);
        }
        if (cacheable.secondaryCache().length > 0) {
            cacheableOperation.setSecondaryCache(cacheable.secondaryCache()[0]);
        }


        parseCacheConfig(method, cacheableOperation);

        return cacheableOperation;
    }

    /**
     * 处理{@link CachePut} 注解
     * @param method
     * @return
     */
    @Override public CachePutOperation parseCachePut(Method method) {
        CachePut cachePut = AnnotationUtils.findAnnotation(method, CachePut.class);
        if (cachePut == null) {
            return null;
        }

        CachePutOperation cachePutOperation = new CachePutOperation();
        cachePutOperation.setCacheNames(cachePut.cacheNames());
        cachePutOperation.setDepict(cachePut.depict());
        cachePutOperation.setKey(cachePut.key());
        cachePutOperation.setCondition(cachePut.condition());
        cachePutOperation.setIgnoreException(cachePut.ignoreException());
        if (cachePut.firstCache().length > 0) {
            cachePutOperation.setFirstCache(cachePut.firstCache()[0]);
        }
        if (cachePut.secondaryCache().length > 0) {
            cachePutOperation.setSecondaryCache(cachePut.secondaryCache()[0]);
        }

        parseCacheConfig(method, cachePutOperation);

        return cachePutOperation;
    }

    /**
     * 处理{@link CacheEvict} 注解
     * @param method
     * @return
     */
    @Override public CacheEvictOperation parseCacheEvict(Method method) {
        CacheEvict cacheEvict = AnnotationUtils.findAnnotation(method, CacheEvict.class);
        if (cacheEvict == null) {
            return null;
        }

        CacheEvictOperation cacheEvictOperation = new CacheEvictOperation();
        cacheEvictOperation.setCacheNames(cacheEvict.cacheNames());
        cacheEvictOperation.setKey(cacheEvict.key());
        cacheEvictOperation.setCondition(cacheEvict.condition());
        cacheEvictOperation.setIgnoreException(cacheEvict.ignoreException());
        cacheEvictOperation.setAllEntries(cacheEvict.allEntries());

        parseCacheConfig(method, cacheEvictOperation);

        return cacheEvictOperation;
    }


    /**
     * 处理{@link CacheConfig} 注解
     * @param method
     * @param cacheOperation
     */
    private void parseCacheConfig(Method method, CacheOperation cacheOperation) {
        CacheConfig cacheConfig = AnnotationUtil.getAnnotation(method.getDeclaringClass(), CacheConfig.class);
        if (cacheConfig == null) {

        } else {
            if (cacheOperation.getCacheNames().length == 0) {
                cacheOperation.setCacheNames(cacheConfig.cacheNames());
            }
            if (StrUtil.isNotBlank(cacheOperation.getKeyGenerator())) {
                cacheOperation.setKeyGenerator(cacheConfig.keyGenerator());
            }
            if (StrUtil.isNotBlank(cacheOperation.getCacheManager())) {
                cacheOperation.setCacheManager(cacheConfig.cacheManager());
            }
            if (cacheOperation.isIgnoreException()) {
                cacheOperation.setCacheManager(cacheConfig.cacheManager());
            }

            // Merge firstConfig
            if (cacheOperation.getFirstCache() == null && cacheConfig.firstCache().length > 0) {
                cacheOperation.setFirstCache(cacheConfig.firstCache()[0]);
            }
            // Merge secondConfig
            if (cacheOperation.getSecondaryCache() == null && cacheConfig.secondaryCache().length > 0) {
                cacheOperation.setSecondaryCache(cacheConfig.secondaryCache()[0]);
            }
        }

    }
}
