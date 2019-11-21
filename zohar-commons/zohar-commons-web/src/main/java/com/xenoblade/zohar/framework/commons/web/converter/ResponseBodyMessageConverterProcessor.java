/*
 * Project: zohar-framework
 *
 * File Created at 2019年10月24日
 *
 * Copyright 2018 CMCC Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ZYHY Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.xenoblade.zohar.framework.commons.web.converter;

import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @Type BodyMessageConverterProcessor
 * @Desc BodyMessageConverterProcessor
 * @author dingming
 * @date 2019年10月24日 11:22 PM
 * @version
 */
public class ResponseBodyMessageConverterProcessor extends AbstractMessageConverterProcessor{

    public ResponseBodyMessageConverterProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public ResponseBodyMessageConverterProcessor(List<HttpMessageConverter<?>> converters,
                                           ContentNegotiationManager manager) {
        super(converters, manager);
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class) ||
                returnType.hasMethodAnnotation(ResponseBody.class));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HttpServletRequest request) throws IOException,
            HttpMediaTypeNotSupportedException {
        parameter = parameter.nestedIfOptional();
        Object arg = readWithMessageConverters(request, parameter, parameter.getNestedGenericParameterType());
        String name = Conventions.getVariableNameForParameter(parameter);

        return adaptArgumentIfNecessary(arg, parameter);
    }

    @Override
    protected <T> Object readWithMessageConverters(HttpServletRequest request,
                                                             MethodParameter parameter,
                                                             Type targetType)
            throws IOException, HttpMediaTypeNotSupportedException,
            HttpMessageNotReadableException {
        Assert.state(request != null, "No HttpServletRequest");
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);

        Object arg = readWithMessageConverters(inputMessage, parameter, targetType);
        if (arg == null && checkRequired(parameter)) {
            throw new HttpMessageNotReadableException("Required request body is missing: " +
                    parameter.getExecutable().toGenericString(), inputMessage);
        }
        return arg;
    }

    protected boolean checkRequired(MethodParameter parameter) {
        RequestBody requestBody = parameter.getParameterAnnotation(RequestBody.class);
        return (requestBody != null && requestBody.required() && !parameter.isOptional());
    }

    @Override public <T> void handleReturnValue(T returnValue, MethodParameter returnType,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws IOException,
            HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);

        // Try even with null return value. ResponseBodyAdvice could get involved.
        writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 *
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2019年10月24日 11:22 PM dingming create
 */
