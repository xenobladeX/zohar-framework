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
package com.xenoblade.zohar.framework.commons.spring.log;

import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorHolder;
import com.xenoblade.zohar.framework.commons.spring.log.api.AccessLoggerInfo;
import com.xenoblade.zohar.framework.commons.spring.log.api.event.AccessLoggerAfterEvent;
import com.xenoblade.zohar.framework.commons.spring.log.api.event.AccessLoggerBeforeEvent;
import com.xenoblade.zohar.framework.commons.utils.id.IDGenerator;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 使用AOP记录访问日志
 * @author xenoblade
 * @since 1.0.0
 */
public class AopAccessLoggerSupport extends StaticMethodMatcherPointcutAdvisor {

    private ApplicationEventPublisher eventPublisher;

    private List<AccessLoggerParser> loggerParsers;

    public AopAccessLoggerSupport(List<AccessLoggerParser> loggerParsers, ApplicationEventPublisher eventPublisher) {
        this.loggerParsers = loggerParsers;
        this.eventPublisher = eventPublisher;
        setAdvice((MethodInterceptor) methodInvocation -> {
            MethodInterceptorHolder methodInterceptorHolder = MethodInterceptorHolder.create(methodInvocation);
            AccessLoggerInfo info = createLogger(methodInterceptorHolder);
            Object response;
            try {
                eventPublisher.publishEvent(new AccessLoggerBeforeEvent(info));
                response = methodInvocation.proceed();
                info.setResponse(response);
            } catch (Throwable e) {
                info.setException(e);
                throw e;
            } finally {
                info.setResponseTime(System.currentTimeMillis());
                //触发监听
                eventPublisher.publishEvent(new AccessLoggerAfterEvent(info));
            }
            return response;
        });
    }

    protected AccessLoggerInfo createLogger(MethodInterceptorHolder holder) {
        AccessLoggerInfo info = new AccessLoggerInfo();
        info.setId(IDGenerator.MD5.generate());

        info.setRequestTime(System.currentTimeMillis());
        info.setParameters(holder.getArgs());
        info.setTarget(holder.getTarget().getClass());
        info.setMethod(holder.getMethod());
        // 链式调用
        loggerParsers.stream()
                .filter(parser -> parser.support(ClassUtils.getUserClass(holder.getTarget()), holder.getMethod()))
                .forEach(parser -> parser.parse(holder, info));

        return info;
    }

    public AopAccessLoggerSupport addParser(AccessLoggerParser parser) {
        if (!loggerParsers.contains(parser)) {
            loggerParsers.add(parser);
        }
        return this;
    }

    @Override public int getOrder() {
        return super.getOrder();
    }

    @Override public boolean matches(Method method, Class<?> aClass) {
        return loggerParsers.stream().anyMatch(parser -> parser.support(aClass, method));
    }
}
