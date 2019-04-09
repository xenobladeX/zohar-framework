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
package com.xenoblade.zohar.framework.commons.log.core;

import cn.hutool.core.annotation.AnnotationUtil;
import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorHolder;
import com.xenoblade.zohar.framework.commons.log.api.AccessLoggerInfo;
import com.xenoblade.zohar.framework.commons.log.api.annotation.AccessLogger;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * DefaultAccessLoggerParser
 * @author xenoblade
 * @since 1.0.0
 */
public class DefaultAccessLoggerParser implements AccessLoggerParser {
    @Override
    public boolean support(Class clazz, Method method) {
        AccessLogger methodAnn = AnnotationUtil.getAnnotation(method, AccessLogger.class);
        AccessLogger classAnn = AnnotationUtil.getAnnotation(clazz, AccessLogger.class);

        if (classAnn != null) {
            return !classAnn.ignore();
        } else if (methodAnn != null) {
            return !methodAnn.ignore();
        } else {
            return false;
        }
    }

    @Override
    public AccessLoggerInfo parse(MethodInterceptorHolder holder, AccessLoggerInfo loggerInfo) {
        AccessLogger methodAnn = holder.findMethodAnnotation(AccessLogger.class);
        AccessLogger classAnn = holder.findClassAnnotation(AccessLogger.class);
        String action = Stream.of(classAnn, methodAnn)
                .filter(Objects::nonNull)
                .map(AccessLogger::value)
                .reduce((c, m) -> c.concat("-").concat(m))
                .orElse("");
        String describe = Stream.of(classAnn, methodAnn)
                .filter(Objects::nonNull)
                .map(AccessLogger::describe)
                .flatMap(Stream::of)
                .reduce((c, s) -> c.concat("\n").concat(s))
                .orElse("");
        loggerInfo.setAction(action);
        loggerInfo.setDescribe(describe);
        return loggerInfo;
    }
}
