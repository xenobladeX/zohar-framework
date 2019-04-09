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
package com.xenoblade.zohar.framework.commons.spring;

import cn.hutool.core.annotation.AnnotationUtil;
import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorContext;
import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.DigestUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * DefaultMethodInterceptorContext
 * @author xenoblade
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public class DefaultMethodInterceptorContext implements MethodInterceptorContext {

    public static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private String id;

    private Method method;

    private Object target;

    private Map<String, Object> args;

    public DefaultMethodInterceptorContext init(MethodInvocation invocation) {

        String id = DigestUtils.md5DigestAsHex(String.valueOf(invocation.getMethod().hashCode()).getBytes());
        String[] argNames = nameDiscoverer.getParameterNames(invocation.getMethod());
        Object[] args = invocation.getArguments();
        Map<String, Object> argMap = new LinkedHashMap<>();
        for (int i = 0, len = args.length; i < len; i++) {
            argMap.put((argNames == null || argNames[i] == null) ? "arg" + i : argNames[i], args[i]);
        }
        return new DefaultMethodInterceptorContext(id,
                invocation.getMethod(),
                invocation.getThis(), argMap);

    }

    public <T extends Annotation> T findMethodAnnotation(Class<T> annClass) {
        return AnnotationUtil.getAnnotation(method, annClass);
    }

    public <T extends Annotation> T findClassAnnotation(Class<T> annClass) {
        return AnnotationUtil.getAnnotation(target.getClass(), annClass);
    }

    public <T extends Annotation> T findAnnotation(Class<T> annClass) {
        T a = findMethodAnnotation(annClass);
        if (a != null) {
            return a;
        }
        return findClassAnnotation(annClass);
    }


    @Override public <T> Optional<T> getParameter(String name) {
        return Optional.empty();
    }

    @Override public <T extends Annotation> T getAnnotation(Class<T> type) {
        return null;
    }

    @Override public Map<String, Object> getParams() {
        return null;
    }

    @Override public Object getInvokeResult() {
        return null;
    }
}
