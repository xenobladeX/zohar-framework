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
package com.xenoblade.zohar.framework.security.basic.support;

import com.xenoblade.zohar.framework.security.basic.config.BasicSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * BasicAuthenticationController
 * @author xenoblade
 * @since 1.0.0
 */
@Controller
@RequestMapping("/authentication")
public class BasicAuthenticationController {

    @Autowired
    private BasicSecurityProperties basicSecurityProperties;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public BasicAuthenticationController() {

    }

    @RequestMapping(value = "/require")
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void requireAuthenticationHtml(HttpServletRequest request, HttpServletResponse response) throws
            IOException{
        redirectStrategy.sendRedirect(request, response, basicSecurityProperties.getFormLogin().getLoginPage());
    }



}
