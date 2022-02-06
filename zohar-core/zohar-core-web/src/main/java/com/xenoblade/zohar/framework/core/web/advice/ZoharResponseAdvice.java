/*
 * Copyright [2022] [xenoblade]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.core.web.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.core.api.dto.BasicErrorCode;
import com.xenoblade.zohar.framework.core.api.dto.Response;
import com.xenoblade.zohar.framework.core.api.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * ZoharResponseAdvice
 *
 * @author xenoblade
 * @since 0.0.1
 */
@Slf4j
@RestControllerAdvice
public class ZoharResponseAdvice implements ResponseBodyAdvice<Object> {

    @Qualifier("webObjectMapper")
    @Autowired
    private ObjectMapper webObjectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body instanceof String){
            try {
                return webObjectMapper.writeValueAsString(Response.of(body));
            } catch (Exception ex) {
                log.warn("convert {} to json failed: ", body);
                throw new SysException(BasicErrorCode.INNER_ERROR, ex);
            }
        } else if (body instanceof Response) {
            return body;
        } else if (body instanceof ResponseEntity) {
            return body;
        }
        return Response.of(body);
    }
}