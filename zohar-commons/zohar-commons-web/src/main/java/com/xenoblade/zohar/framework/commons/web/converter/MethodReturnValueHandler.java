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
package com.xenoblade.zohar.framework.commons.web.converter;

import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * MethodReturnValueHandler
 * @author xenoblade
 * @since 1.0.0
 */
public interface MethodReturnValueHandler {

    /**
     * Whether the given {@linkplain MethodParameter method return type} is
     * supported by this handler.
     * @param returnType the method return type to check
     * @return {@code true} if this handler supports the supplied return type;
     * {@code false} otherwise
     */
    boolean supportsReturnType(MethodParameter returnType);

    /**
     * Handle the given return value by adding attributes to the model and
     * setting a view or setting the
     * {@link ModelAndViewContainer#setRequestHandled} flag to {@code true}
     * to indicate the response has been handled directly.
     * @param returnValue the value returned from the handler method
     * @param returnType the type of the return value. This type must have
     * previously been passed to {@link #supportsReturnType} which must
     * have returned {@code true}.
     * @param request the current request
     * @param response thre current response
     * @throws Exception if the return value handling results in an error
     */
    <T> void handleReturnValue(@Nullable T returnValue, MethodParameter returnType,
                               HttpServletRequest request, HttpServletResponse response) throws
            IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException;

}
