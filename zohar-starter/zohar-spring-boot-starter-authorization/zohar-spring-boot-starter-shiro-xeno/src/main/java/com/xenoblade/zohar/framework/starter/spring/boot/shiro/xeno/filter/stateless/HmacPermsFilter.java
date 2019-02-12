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
package com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.filter.stateless;

import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.config.MessageConfig;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.util.Commons;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 基于HMAC（散列消息认证码）的无状态认证过滤器--资源验证过滤器
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class HmacPermsFilter extends StatelessFilter{

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = getSubject(request, response);
        if ((null == subject || !subject.isAuthenticated()) && isHmacSubmission(request)) {
            AuthenticationToken token = createHmacToken(request, response);
            try {
                subject = getSubject(request, response);
                subject.login(token);
                return this.checkPerms(subject,mappedValue);
            } catch (AuthenticationException e) {
                log.error(request.getRemoteHost()+" HMAC鉴权  "+e.getMessage());
                Commons.restFailed(WebUtils.toHttp(response)
                        , MessageConfig.REST_CODE_AUTH_UNAUTHORIZED,e.getMessage());
            }
        }
        return false;
    }

}
