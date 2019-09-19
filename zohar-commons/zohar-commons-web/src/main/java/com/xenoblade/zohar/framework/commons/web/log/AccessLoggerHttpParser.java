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

import com.xenoblade.zohar.framework.commons.log.api.AccessLoggerInfo;
import com.xenoblade.zohar.framework.commons.log.core.AccessLoggerParser;
import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorContext;
import com.xenoblade.zohar.framework.commons.spring.aop.MethodInterceptorHolder;
import com.xenoblade.zohar.framework.commons.web.utils.WebUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * AccessLoggerHttpParser
 * @author xenoblade
 * @since 1.0.0
 */
public class AccessLoggerHttpParser implements AccessLoggerParser {
    @Override
    public boolean support(Class clazz, Method method) {
        return  WebUtil.getHttpServletRequest() != null;
    }

    @Override
    public AccessLoggerInfo inAccess(MethodInterceptorContext methodInterceptorContext, AccessLoggerInfo loggerInfo) {
        HttpServletRequest request = WebUtil.getHttpServletRequest();
        if (null != request) {
            AccessLoggerHttpContext context = new AccessLoggerHttpContext();
            context.setHttpHeaders(WebUtil.getHeaders(request));
            context.setIp(WebUtil.getIpAddr(request));
            context.setHttpMethod(request.getMethod());
            context.setUrl(request.getRequestURL().toString());
            if (loggerInfo != null) {
                loggerInfo.getContexts().put(context.contextType(), context);
            }
        }
        return loggerInfo;
    }

    @Override public AccessLoggerInfo outAccess(MethodInterceptorContext methodInterceptorContext,
                                                AccessLoggerInfo loggerInfo) {
        return loggerInfo;
    }
}
