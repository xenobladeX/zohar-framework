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

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.config.MessageConfig;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.util.Commons;

/**
 * 抽象认证过滤器,扩展自AccessControlFilter增加了针对ajax请求的处理。
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class XenoAccessControlFilter extends AccessControlFilter {

    /**
     * 定位到登陆界面，返回false过滤器链停止
     */
    protected boolean respondLogin(ServletRequest request, ServletResponse response) throws IOException{
        if (Commons.isAjax(WebUtils.toHttp(request))) {
            Commons.ajaxFailed(WebUtils.toHttp(response)
                    ,HttpServletResponse.SC_UNAUTHORIZED
                    ,MessageConfig.REST_CODE_AUTH_UNAUTHORIZED
                    ,MessageConfig.REST_MESSAGE_AUTH_UNAUTHORIZED);
            return false;// 过滤器链停止
        }
        saveRequestAndRedirectToLogin(request, response);
        return false;
    }

    /**
     * 定位到指定界面，返回false过滤器链停止
     */
    protected boolean respondRedirect(ServletRequest request, ServletResponse response,String redirectUrl) throws IOException{
        WebUtils.issueRedirect(request, response, redirectUrl);
        return false;
    }

}
