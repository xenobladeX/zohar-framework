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
package com.xenoblade.zohar.framework.cache.aspectj;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheConfig;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheEvict;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CachePut;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.Cacheable;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.FirstCache;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.SecondaryCache;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CacheEvictOperation;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CachePutOperation;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.operation.CacheableOperation;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.parser.CacheAnnotationParser;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.parser.DefaultCacheAnnotationParser;
import com.xenoblade.zohar.framework.cache.aspectj.expression.CacheOperationExpressionEvaluator;
import com.xenoblade.zohar.framework.cache.aspectj.support.CacheOperationInvoker;
import com.xenoblade.zohar.framework.cache.aspectj.support.KeyGenerator;
import com.xenoblade.zohar.framework.cache.aspectj.support.SimpleKeyGenerator;
import com.xenoblade.zohar.framework.cache.core.cache.Cache;
import com.xenoblade.zohar.framework.cache.core.config.FirstCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.MultiLayerCacheConfig;
import com.xenoblade.zohar.framework.cache.core.config.SecondaryCacheConfig;
import com.xenoblade.zohar.framework.cache.core.manager.CacheManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;


/**
 * MultiLayerAspect
 * @author xenoblade
 * @since 1.0.0
 */
@Aspect
@Slf4j
public class MultiLayerAspect {

    private static final String CACHE_KEY_ERROR_MESSAGE = "缓存Key {} 不能为NULL";
    private static final String CACHE_NAME_ERROR_MESSAGE = "缓存名称不能为NULL";


    /**
     * SpEL表达式计算器
     */
    private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator();

    private CacheManager cacheManager;

    private KeyGenerator keyGenerator = new SimpleKeyGenerator();

    private CacheAnnotationParser cacheAnnotationParser = new DefaultCacheAnnotationParser();

    public MultiLayerAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public MultiLayerAspect(CacheManager cacheManager, KeyGenerator keyGenerator) {
        this.cacheManager = cacheManager;
        this.keyGenerator = keyGenerator;
    }

    @Pointcut("@annotation(com.xenoblade.zohar.framework.cache.aspectj.annotation.Cacheable)")
    public void cacheablePointcut() {
    }

    @Pointcut("@annotation(com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheEvict)")
    public void cacheEvictPointcut() {
    }

    @Pointcut("@annotation(com.xenoblade.zohar.framework.cache.aspectj.annotation.CachePut)")
    public void cachePutPointcut() {
    }

    @Around("cacheablePointcut()")
    public Object cacheablePointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheOperationInvoker aopAllianceInvoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);
        // 获取注解操作
        CacheableOperation cacheableOperation = cacheAnnotationParser.parseCacheable(method);
        try {
            // 执行查询缓存方法
            return executeCacheable(aopAllianceInvoker, cacheableOperation, method, joinPoint.getArgs(), joinPoint.getTarget());
        } catch (SerializationException e) {
            // 如果是序列化异常需要先删除原有缓存
            String[] cacheNames = cacheableOperation.getCacheNames();
            // 删除缓存
            delete(cacheNames, cacheableOperation.getKey(), method, joinPoint.getArgs(), joinPoint.getTarget());

            // 忽略操作缓存过程中遇到的异常
            if (cacheableOperation.isIgnoreException()) {
                log.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cacheableOperation.isIgnoreException()) {
                log.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        }
    }

    @Around("cacheEvictPointcut()")
    public Object cacheEvictPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheOperationInvoker aopAllianceInvoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);
        // 获取注解操作
        CacheEvictOperation cacheEvictOperation = cacheAnnotationParser.parseCacheEvict(method);

        try {
            // 执行查询缓存方法
            return executeEvict(aopAllianceInvoker, cacheEvictOperation, method, joinPoint.getArgs(), joinPoint.getTarget());
        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cacheEvictOperation.isIgnoreException()) {
                log.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        }
    }

    @Around("cachePutPointcut()")
    public Object cachePutPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheOperationInvoker aopAllianceInvoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);
        // 获取注解
        CachePutOperation cachePutOperation = cacheAnnotationParser.parseCachePut(method);

        try {
            // 执行查询缓存方法
            return executePut(aopAllianceInvoker, cachePutOperation, method, joinPoint.getArgs(), joinPoint.getTarget());
        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cachePutOperation.isIgnoreException()) {
                log.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        }
    }


    /**
     * 执行Cacheable切面
     *
     * @param invoker   缓存注解的回调方法
     * @param cacheableOperation {@link CacheableOperation}
     * @param method    {@link Method}
     * @param args      注解方法参数
     * @param target    target
     * @return {@link Object}
     */
    private Object executeCacheable(CacheOperationInvoker invoker, CacheableOperation cacheableOperation,
                                    Method method, Object[] args, Object target) {

        // 解析SpEL表达式获取cacheName和key
        String[] cacheNames = cacheableOperation.getCacheNames();
        Assert.notEmpty(cacheNames, CACHE_NAME_ERROR_MESSAGE);
        String cacheName = cacheNames[0];
        Object key = generateKey(cacheableOperation.getKey(), method, args, target);
        Assert.notNull(key, String.format(CACHE_KEY_ERROR_MESSAGE, cacheableOperation.getKey()));

        // 从解决中获取缓存配置
        FirstCache firstCache = cacheableOperation.getFirstCache();
        SecondaryCache secondaryCache = cacheableOperation.getSecondaryCache();
        FirstCacheConfig firstCacheConfig = new FirstCacheConfig(firstCache.initialCapacity(), firstCache.maximumSize(),
                firstCache.expireTime(), firstCache.timeUnit(), firstCache.expireMode());

        SecondaryCacheConfig secondaryCacheConfig = new SecondaryCacheConfig(secondaryCache.expireTime(),
                secondaryCache.preloadTime(), secondaryCache.timeUnit(), secondaryCache.forceRefresh(),
                secondaryCache.isAllowNullValue(), secondaryCache.magnification(), secondaryCache.keyEncodeType(), secondaryCache.keyHashType());

        MultiLayerCacheConfig multiLayerCacheConfig = new MultiLayerCacheConfig(firstCacheConfig, secondaryCacheConfig,
                cacheableOperation.getDepict());

        // 通过cacheName和缓存配置获取Cache
        Cache cache = cacheManager.getCache(cacheName, multiLayerCacheConfig);

        // 通Cache获取值
        return cache.get(key, () -> invoker.invoke());
    }


    /**
     * 执行 CacheEvict 切面
     *
     * @param invoker    缓存注解的回调方法
     * @param cacheEvictOperation {@link CacheEvictOperation}
     * @param method     {@link Method}
     * @param args       注解方法参数
     * @param target     target
     * @return {@link Object}
     */
    private Object executeEvict(CacheOperationInvoker invoker, CacheEvictOperation cacheEvictOperation,
                                Method method, Object[] args, Object target) {

        // 解析SpEL表达式获取cacheName和key
        String[] cacheNames = cacheEvictOperation.getCacheNames();
        Assert.notEmpty(cacheNames, CACHE_NAME_ERROR_MESSAGE);
        // 判断是否删除所有缓存数据
        if (cacheEvictOperation.isAllEntries()) {
            // 删除所有缓存数据（清空）
            for (String cacheName : cacheNames) {
                Collection<Cache> caches = cacheManager.getCache(cacheName);
                for (Cache cache : caches) {
                    cache.clear();
                }
            }
        } else {
            // 删除指定key
            delete(cacheNames, cacheEvictOperation.getKey(), method, args, target);
        }

        // 执行方法
        return invoker.invoke();
    }


    /**
     * 执行 CachePut 切面
     *
     * @param invoker  缓存注解的回调方法
     * @param cachePutOperation {@link CachePutOperation}
     * @param method   {@link Method}
     * @param args     注解方法参数
     * @param target   target
     * @return {@link Object}
     */
    private Object executePut(CacheOperationInvoker invoker, CachePutOperation cachePutOperation, Method method, Object[] args, Object target) {


        String[] cacheNames = cachePutOperation.getCacheNames();
        Assert.notEmpty(cachePutOperation.getCacheNames(), CACHE_NAME_ERROR_MESSAGE);
        // 解析SpEL表达式获取 key
        Object key = generateKey(cachePutOperation.getKey(), method, args, target);
        Assert.notNull(key, String.format(CACHE_KEY_ERROR_MESSAGE, cachePutOperation.getKey()));

        // 从解决中获取缓存配置
        FirstCache firstCache = cachePutOperation.getFirstCache();
        SecondaryCache secondaryCache = cachePutOperation.getSecondaryCache();
        FirstCacheConfig firstCacheConfig = new FirstCacheConfig(firstCache.initialCapacity(), firstCache.maximumSize(),
                firstCache.expireTime(), firstCache.timeUnit(), firstCache.expireMode());

        SecondaryCacheConfig secondaryCacheConfig = new SecondaryCacheConfig(secondaryCache.expireTime(),
                secondaryCache.preloadTime(), secondaryCache.timeUnit(), secondaryCache.forceRefresh(),
                secondaryCache.isAllowNullValue(), secondaryCache.magnification(), secondaryCache.keyEncodeType(), secondaryCache.keyHashType());

        MultiLayerCacheConfig multiLayerCacheConfig = new MultiLayerCacheConfig(firstCacheConfig, secondaryCacheConfig,
                cachePutOperation.getDepict());

        // 指定调用方法获取缓存值
        Object result = invoker.invoke();

        for (String cacheName : cacheNames) {
            // 通过cacheName和缓存配置获取Cache
            Cache cache = cacheManager.getCache(cacheName, multiLayerCacheConfig);
            cache.put(key, result);
        }

        return result;
    }

    /**
     * 删除执行缓存名称上的指定key
     *
     * @param cacheNames 缓存名称
     * @param keySpEL    key的SpEL表达式
     * @param method     {@link Method}
     * @param args       参数列表
     * @param target     目标类
     */
    private void delete(String[] cacheNames, String keySpEL, Method method, Object[] args, Object target) {
        Object key = generateKey(keySpEL, method, args, target);
        Assert.notNull(key, StrUtil.format(CACHE_KEY_ERROR_MESSAGE, keySpEL));
        for (String cacheName : cacheNames) {
            Collection<Cache> caches = cacheManager.getCache(cacheName);
            for (Cache cache : caches) {
                cache.evict(key);
            }
        }
    }


    private CacheOperationInvoker getCacheOperationInvoker(ProceedingJoinPoint joinPoint) {
        return () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                throw new CacheOperationInvoker.ThrowableWrapperException(ex);
            }
        };
    }

    /**
     * 解析SpEL表达式，获取注解上的key属性值
     *
     * @return Object
     */
    private Object generateKey(String keySpEl, Method method, Object[] args, Object target) {

        // 获取注解上的key属性值
        Class<?> targetClass = getTargetClass(target);
        if (StringUtils.hasText(keySpEl)) {
            EvaluationContext evaluationContext = evaluator.createEvaluationContext(method, args, target,
                    targetClass, CacheOperationExpressionEvaluator.NO_RESULT);

            AnnotatedElementKey methodCacheKey = new AnnotatedElementKey(method, targetClass);
            // 兼容传null值得情况
            Object keyValue = evaluator.key(keySpEl, methodCacheKey, evaluationContext);
            return Objects.isNull(keyValue) ? "null" : keyValue;
        }
        return this.keyGenerator.generate(target, method, args);
    }


    /**
     * 获取类信息
     *
     * @param target Object
     * @return targetClass
     */
    private Class<?> getTargetClass(Object target) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }

    private CacheConfig getCacheConfig(Object target) {
        Class<?> targetClass = getTargetClass(target);
        return AnnotationUtil.getAnnotation(targetClass, CacheConfig.class);
    }

    /**
     * 获取Method
     *
     * @param pjp ProceedingJoinPoint
     * @return {@link Method}
     */
    private Method getSpecificmethod(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        // The method may be on an interface, but we need attributes from the
        // target class. If the target class is null, the method will be
        // unchanged.
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(pjp.getTarget());
        if (targetClass == null && pjp.getTarget() != null) {
            targetClass = pjp.getTarget().getClass();
        }
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the
        // original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        return specificMethod;
    }


}
