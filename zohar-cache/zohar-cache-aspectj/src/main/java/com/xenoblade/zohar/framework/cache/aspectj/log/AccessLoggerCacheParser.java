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

import cn.hutool.core.annotation.AnnotationUtil;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheEvict;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CachePut;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.Cacheable;
import com.xenoblade.zohar.framework.commons.log.api.AccessLoggerInfo;
import com.xenoblade.zohar.framework.commons.log.core.AccessLoggerParser;
import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorContext;

import java.lang.reflect.Method;

/**
 * AccessLoggerCacheParser
 * @author xenoblade
 * @since 1.0.0
 */
public class AccessLoggerCacheParser implements AccessLoggerParser {

    @Override public boolean support(Class clazz, Method method) {

        Cacheable cacheableMethodAnn = AnnotationUtil.getAnnotation(method, Cacheable.class);
        Cacheable cacheableClassAnn = AnnotationUtil.getAnnotation(clazz, Cacheable.class);

        CacheEvict cacheEvictMethodAnn = AnnotationUtil.getAnnotation(method, CacheEvict.class);
        CacheEvict cacheEvictClassAnn = AnnotationUtil.getAnnotation(clazz, CacheEvict.class);

        CachePut cachePutMethodAnn = AnnotationUtil.getAnnotation(method, CachePut.class);
        CachePut cachePutClassAnn = AnnotationUtil.getAnnotation(clazz, CachePut.class);


        return (cacheableMethodAnn != null || cacheableClassAnn != null) ||
                (cacheEvictMethodAnn != null || cacheEvictClassAnn != null) ||
                (cachePutMethodAnn != null || cachePutClassAnn != null);
    }

    @Override public AccessLoggerInfo inAccess(MethodInterceptorContext methodInterceptorContext,
                                               AccessLoggerInfo loggerInfo) {
        return loggerInfo;
    }

    @Override public AccessLoggerInfo outAccess(MethodInterceptorContext methodInterceptorContext,
                                                AccessLoggerInfo loggerInfo) {

        AccessLoggerCacheContext accessLoggerCacheContext = AccessLoggerCacheUtils.getContext();
        if (accessLoggerCacheContext != null) {
            if (loggerInfo != null) {
                loggerInfo.getContexts().put(accessLoggerCacheContext.contextType(), accessLoggerCacheContext);
            }
        }
        return loggerInfo;
    }
}
