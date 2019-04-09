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
package com.xenoblade.zohar.framework.commons.log.core.aop;

import cn.hutool.core.util.IdUtil;
import com.xenoblade.zohar.framework.commons.log.core.config.AccessLoggerInterceptorConfiguration;
import com.xenoblade.zohar.framework.commons.log.api.AccessLoggerInfo;
import com.xenoblade.zohar.framework.commons.log.api.event.AccessLoggerAfterEvent;
import com.xenoblade.zohar.framework.commons.log.api.event.AccessLoggerBeforeEvent;
import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorHolder;
import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorContext;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ClassUtils;

/**
 * AccessLoggerInterceptor
 * @author xenoblade
 * @since 1.0.0
 */
@AllArgsConstructor
public class AccessLoggerInterceptor implements MethodInterceptor {

    private ApplicationEventPublisher eventPublisher;

    private AccessLoggerInterceptorConfiguration configuration;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        MethodInterceptorHolder.current().add(invocation);
        AccessLoggerInfo info = createLogger(MethodInterceptorHolder.current().peek());
        Object response;
        try {
            eventPublisher.publishEvent(new AccessLoggerBeforeEvent(info));
            response = invocation.proceed();
            info.setResponse(response);
        } catch (Throwable e) {
            info.setException(e);
            throw e;
        } finally {
            info.setResponseTime(System.currentTimeMillis());
            //触发监听
            eventPublisher.publishEvent(new AccessLoggerAfterEvent(info));
            MethodInterceptorHolder.current().poll();
            if ( MethodInterceptorHolder.current().peek() == null) {
                MethodInterceptorHolder.clear();
            }
        }
        return response;
    }


    protected AccessLoggerInfo createLogger(MethodInterceptorContext methodInterceptorContext) {
        AccessLoggerInfo info = new AccessLoggerInfo();
        info.setId(IdUtil.objectId());

        info.setRequestTime(System.currentTimeMillis());
        info.setParameters(methodInterceptorContext.getParams());
        info.setTarget(methodInterceptorContext.getTarget().getClass());
        info.setMethod(methodInterceptorContext.getMethod());
        // 链式调用
        configuration.getLoggerParsers().stream()
                .filter(parser -> parser.support(ClassUtils.getUserClass(methodInterceptorContext.getTarget()), methodInterceptorContext.getMethod()))
                .forEach(parser -> parser.parse(methodInterceptorContext, info));

        return info;
    }

}
