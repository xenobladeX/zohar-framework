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
package com.xenoblade.zohar.framework.commons.web.log;

import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorHolder;
import com.xenoblade.zohar.framework.commons.spring.log.AccessLoggerParser;
import com.xenoblade.zohar.framework.commons.spring.log.api.AccessLogger;
import com.xenoblade.zohar.framework.commons.spring.log.api.AccessLoggerInfo;
import com.xenoblade.zohar.framework.commons.web.WebUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * HttpAccessLoggerParser
 * @author xenoblade
 * @since 1.0.0
 */
public class HttpAccessLoggerParser implements AccessLoggerParser {
    @Override
    public boolean support(Class clazz, Method method) {
        RestController ann = AnnotationUtils.findAnnotation(clazz, RestController.class);
        //注解了并且未取消
        return null != ann;
    }

    @Override
    public AccessLoggerInfo parse(MethodInterceptorHolder holder, AccessLoggerInfo loggerInfo) {
        HttpServletRequest request = WebUtils.getHttpServletRequest();
        if (null != request) {
            AccessLoggerHttpContext context = new AccessLoggerHttpContext();
            context.setHttpHeaders(WebUtils.getHeaders(request));
            context.setIp(WebUtils.getIpAddr(request));
            context.setHttpMethod(request.getMethod());
            context.setUrl(request.getRequestURL().toString());
            loggerInfo.setContext(context);
        }
        return loggerInfo;
    }
}
