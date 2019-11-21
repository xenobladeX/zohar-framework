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
package com.xenoblade.zohar.framework.security.basic.handler;

import cn.hutool.core.util.ClassUtil;
import com.xenoblade.zohar.framework.commons.web.converter.MethodReturnValueHandler;
import com.xenoblade.zohar.framework.security.basic.config.ELoginType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * BasicAuthenticationSuccessHandler
 * @author xenoblade
 * @since 1.0.0
 */
@Setter
@Slf4j
public class BasicAuthenticationSuccessHandler extends
        SavedRequestAwareAuthenticationSuccessHandler{

    private ELoginType loginType = ELoginType.BODY;

    private LoginResponseConverter loginResponseConverter = new DefaultLoginResponseConverter();

    private List<MethodReturnValueHandler> methodReturnValueHandlerList;

    @Override public void onAuthenticationSuccess(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  Authentication authentication)
            throws ServletException, IOException {

        switch (loginType) {
            case BODY:
            {
                ResponseEntity responseEntity = loginResponseConverter.convert(authentication);
                Method method = ClassUtil.getPublicMethod(loginResponseConverter.getClass(), "convert", Authentication.class);
                MethodParameter methodParameter = new MethodParameter(method, -1);
                try {
                    for (MethodReturnValueHandler methodReturnValueHandler : methodReturnValueHandlerList) {
                        if(methodReturnValueHandler.supportsReturnType(methodParameter)) {
                            methodReturnValueHandler.handleReturnValue(responseEntity, methodParameter, request, response);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    // TODO exception handler
                }
                break;
            }
            case REDIRECT:
            {
                super.onAuthenticationSuccess(request, response, authentication);

                break;
            }
        }
    }
}
