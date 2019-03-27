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
package com.xenoblade.zohar.framework.commons.web.msg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xenoblade.zohar.framework.commons.api.enums.IZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.enums.ZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import com.xenoblade.zohar.framework.commons.utils.jackson.JacksonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * controller 通用响应消息
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class ResponseMessage<T> implements Serializable {

    private static final long serialVersionUID = 8992436576262574064L;

    protected String message;

    protected T result;

    private Long timestamp;

    private Integer status;

    private String code;


    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public T getResult() {
        return result;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public static <T> ResponseMessage<T> error(String message) {
        return error(ZoharErrorCode.INNER_ERROR, message);
    }

    public static <T> ResponseMessage<T> error(IZoharErrorCode errorCode) {
        return error(errorCode, errorCode.getMessage());
    }

    public static <T> ResponseMessage<T> error(IZoharErrorCode errorCode, String message) {
        ResponseMessage<T> msg = new ResponseMessage<>();
        msg.errorCode(errorCode);
        msg.message = message;
        return msg.putTimeStamp();
    }

    public static <T> ResponseMessage<T> error(ZoharException ex) {
        ResponseMessage<T> msg = new ResponseMessage<>();
        msg.message = ex.getMessage();
        msg.status = ex.getStatus();
        msg.code = ex.getCode();
        return msg.putTimeStamp();
    }

    public static <T> ResponseMessage<T> ok() {
        return ok(null);
    }

    public static <T> ResponseMessage<T> ok(T result) {
        return new ResponseMessage<T>()
                .result(result)
                .putTimeStamp()
                .errorCode(ZoharErrorCode.OK);
    }

    public ResponseMessage<T> result(T result) {
        this.result = result;
        return this;
    }

    private ResponseMessage<T> putTimeStamp() {
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    public ResponseMessage<T> message(String message) {
        this.message = message;
        return this;
    }

    public ResponseMessage<T> code(String code) {
        this.code = code;
        return this;
    }

    public ResponseMessage<T> errorCode(IZoharErrorCode errorCode) {
        this.status = errorCode.getCode();
        this.code = errorCode.name();
        return this;
    }

    protected Set<String> getStringListFromMap(Map<Class<?>, Set<String>> map, Class type) {
        return map.computeIfAbsent(type, k -> new HashSet<>());
    }

    @Override
    @SneakyThrows(JsonProcessingException.class)
    public String toString() {
        return JacksonUtil.getObjectMapper().writeValueAsString(this);
    }


    public void responseJson(HttpServletResponse response
            , int respondStatus) {
        response.setStatus(respondStatus);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String json = JacksonUtil.getObjectMapper().writeValueAsString(this);
            out.write(json);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (out != null)
                out.close();
        }
    }

}
