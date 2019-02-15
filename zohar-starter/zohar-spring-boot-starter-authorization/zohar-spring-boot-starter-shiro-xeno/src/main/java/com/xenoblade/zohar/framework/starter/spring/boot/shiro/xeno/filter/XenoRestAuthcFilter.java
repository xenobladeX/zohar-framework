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
package com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.filter;

import com.xenoblade.zohar.framework.commons.api.EErrorCode;
import com.xenoblade.zohar.framework.commons.web.message.ResponseMessage;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.config.ShiroProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * XenoRestAuthcFilter
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class XenoRestAuthcFilter extends AccessControlFilter {

    private ShiroProperties properties;

    @Override protected boolean isAccessAllowed(ServletRequest request,
                                                ServletResponse response, Object o) {
        if (this.isLoginRequest(request, response)) {
            return true;
        }

        if (null != getSubject(request, response)
                && getSubject(request, response).isAuthenticated()) {
            return true;
        }
        return false;
    }

    @Override protected boolean onAccessDenied(ServletRequest request,
                                               ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        ResponseMessage responseMessage = ResponseMessage.error(EErrorCode.UNAUTHORIZED);
        responseMessage.responseJson(httpServletResponse, HttpStatus.UNAUTHORIZED.value());
        return false;
    }

    public void setProperties(ShiroProperties properties) {
        this.properties = properties;
    }
}
