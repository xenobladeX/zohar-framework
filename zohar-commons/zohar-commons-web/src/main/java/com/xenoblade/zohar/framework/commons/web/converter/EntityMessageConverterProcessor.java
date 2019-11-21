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

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * EntityMessageConverterProcessor
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class EntityMessageConverterProcessor extends AbstractMessageConverterProcessor{

    private static final Set<HttpMethod> SAFE_METHODS = EnumSet.of(HttpMethod.GET, HttpMethod.HEAD);

    /**
     * Basic constructor with converters only. Suitable for resolving
     * {@code HttpEntity}. For handling {@code ResponseEntity} consider also
     * providing a {@code ContentNegotiationManager}.
     */
    public EntityMessageConverterProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    /**
     * Basic constructor with converters and {@code ContentNegotiationManager}.
     * Suitable for resolving {@code HttpEntity} and handling {@code ResponseEntity}
     * without {@code Request~} or {@code ResponseBodyAdvice}.
     */
    public EntityMessageConverterProcessor(List<HttpMessageConverter<?>> converters,
                                     ContentNegotiationManager manager) {

        super(converters, manager);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (HttpEntity.class == parameter.getParameterType() ||
                RequestEntity.class == parameter.getParameterType());
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (HttpEntity.class.isAssignableFrom(returnType.getParameterType()) &&
                !RequestEntity.class.isAssignableFrom(returnType.getParameterType()));
    }

    @Override public Object resolveArgument(MethodParameter parameter,
                                            HttpServletRequest request) throws IOException,
            HttpMediaTypeNotSupportedException {
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
        Type paramType = getHttpEntityType(parameter);
        if (paramType == null) {
            throw new IllegalArgumentException("HttpEntity parameter '" + parameter.getParameterName() +
                    "' in method " + parameter.getMethod() + " is not parameterized");
        }

        Object body = readWithMessageConverters(request, parameter, paramType);
        if (RequestEntity.class == parameter.getParameterType()) {
            return new RequestEntity<>(body, inputMessage.getHeaders(),
                    inputMessage.getMethod(), inputMessage.getURI());
        }
        else {
            return new HttpEntity<>(body, inputMessage.getHeaders());
        }
    }



    @Override
    public <T> void handleReturnValue(T returnValue, MethodParameter returnType,
                                                HttpServletRequest request,
                                      HttpServletResponse response) throws IOException,
            HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        if (returnValue == null) {
            return;
        }

        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);

        Assert.isInstanceOf(HttpEntity.class, returnValue);
        HttpEntity<?> responseEntity = (HttpEntity<?>) returnValue;

        HttpHeaders outputHeaders = outputMessage.getHeaders();
        HttpHeaders entityHeaders = responseEntity.getHeaders();
        if (!entityHeaders.isEmpty()) {
            entityHeaders.forEach((key, value) -> {
                if (HttpHeaders.VARY.equals(key) && outputHeaders.containsKey(HttpHeaders.VARY)) {
                    List<String> values = getVaryRequestHeadersToAdd(outputHeaders, entityHeaders);
                    if (!values.isEmpty()) {
                        outputHeaders.setVary(values);
                    }
                }
                else {
                    outputHeaders.put(key, value);
                }
            });
        }

        if (responseEntity instanceof ResponseEntity) {
            int returnStatus = ((ResponseEntity<?>) responseEntity).getStatusCodeValue();
            outputMessage.getServletResponse().setStatus(returnStatus);
            if (returnStatus == 200) {
                if (SAFE_METHODS.contains(inputMessage.getMethod())
                        && isResourceNotModified(inputMessage, outputMessage)) {
                    // Ensure headers are flushed, no body should be written.
                    outputMessage.flush();
                    // Skip call to converters, as they may update the body.
                    return;
                }
            }
            else if (returnStatus / 100 == 3) {
                String location = outputHeaders.getFirst("location");
                if (location != null) {

                }
            }
        }

        // Try even with null body. ResponseBodyAdvice could get involved.
        writeWithMessageConverters(responseEntity.getBody(), returnType, inputMessage, outputMessage);

        // Ensure headers are flushed even if no body was written.
        outputMessage.flush();
    }

    private List<String> getVaryRequestHeadersToAdd(HttpHeaders responseHeaders, HttpHeaders entityHeaders) {
        List<String> entityHeadersVary = entityHeaders.getVary();
        List<String> vary = responseHeaders.get(HttpHeaders.VARY);
        if (vary != null) {
            List<String> result = new ArrayList<>(entityHeadersVary);
            for (String header : vary) {
                for (String existing : StringUtils.tokenizeToStringArray(header, ",")) {
                    if ("*".equals(existing)) {
                        return Collections.emptyList();
                    }
                    for (String value : entityHeadersVary) {
                        if (value.equalsIgnoreCase(existing)) {
                            result.remove(value);
                        }
                    }
                }
            }
            return result;
        }
        return entityHeadersVary;
    }

    private boolean isResourceNotModified(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        ServletWebRequest servletWebRequest =
                new ServletWebRequest(request.getServletRequest(), response.getServletResponse());
        HttpHeaders responseHeaders = response.getHeaders();
        String etag = responseHeaders.getETag();
        long lastModifiedTimestamp = responseHeaders.getLastModified();
        if (request.getMethod() == HttpMethod.GET || request.getMethod() == HttpMethod.HEAD) {
            responseHeaders.remove(HttpHeaders.ETAG);
            responseHeaders.remove(HttpHeaders.LAST_MODIFIED);
        }

        return servletWebRequest.checkNotModified(etag, lastModifiedTimestamp);
    }

    @Nullable
    private Type getHttpEntityType(MethodParameter parameter) {
        Assert.isAssignable(HttpEntity.class, parameter.getParameterType());
        Type parameterType = parameter.getGenericParameterType();
        if (parameterType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) parameterType;
            if (type.getActualTypeArguments().length != 1) {
                throw new IllegalArgumentException("Expected single generic parameter on '" +
                        parameter.getParameterName() + "' in method " + parameter.getMethod());
            }
            return type.getActualTypeArguments()[0];
        }
        else if (parameterType instanceof Class) {
            return Object.class;
        }
        else {
            return null;
        }
    }

}
