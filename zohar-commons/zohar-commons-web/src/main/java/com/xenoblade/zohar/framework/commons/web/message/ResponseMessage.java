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
package com.xenoblade.zohar.framework.commons.web.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.xenoblade.zohar.framework.commons.api.EErrorCode;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import com.xenoblade.zohar.framework.commons.utils.JacksonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ResponseMessage
 * @author xenoblade
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
        return error(EErrorCode.INNER_ERROR, message);
    }

    public static <T> ResponseMessage<T> error(EErrorCode errorCode) {
        return error(errorCode, errorCode.getErrorDescription());
    }

    public static <T> ResponseMessage<T> error(EErrorCode errorCode, String message) {
        ResponseMessage<T> msg = new ResponseMessage<>();
        msg.message = message;
        msg.errorCode(errorCode);
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
                .errorCode(EErrorCode.OK);
    }

    public ResponseMessage<T> result(T result) {
        this.result = result;
        return this;
    }

    private ResponseMessage<T> putTimeStamp() {
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    public ResponseMessage<T> errorCode(EErrorCode errorCode) {
        this.status = errorCode.getCode();
        this.code = errorCode.name();
        return this;
    }


    /**
     * 过滤字段：指定需要序列化的字段
     */
    private transient Map<Class<?>, Set<String>> includes;

    /**
     * 过滤字段：指定不需要序列化的字段
     */
    private transient Map<Class<?>, Set<String>> excludes;

    public ResponseMessage() {

    }

    public ResponseMessage<T> include(Class<?> type, String... fields) {
        return include(type, Arrays.asList(fields));
    }

    public ResponseMessage<T> include(Class<?> type, Collection<String> fields) {
        if (includes == null) {
            includes = new HashMap<>();
        }
        if (fields == null || fields.isEmpty()) {
            return this;
        }
        fields.forEach(field -> {
            if (field.contains(".")) {
                String tmp[] = field.split("[.]", 2);
                try {
                    Field field1 = type.getDeclaredField(tmp[0]);
                    if (field1 != null) {
                        include(field1.getType(), tmp[1]);
                    }
                } catch (Exception ignore) {
                }
            } else {
                getStringListFromMap(includes, type).add(field);
            }
        });
        return this;
    }

    public ResponseMessage<T> exclude(Class type, Collection<String> fields) {
        if (excludes == null) {
            excludes = new HashMap<>();
        }
        if (fields == null || fields.isEmpty()) {
            return this;
        }
        fields.forEach(field -> {
            if (field.contains(".")) {
                String tmp[] = field.split("[.]", 2);
                try {
                    Field field1 = type.getDeclaredField(tmp[0]);
                    if (field1 != null) {
                        exclude(field1.getType(), tmp[1]);
                    }
                } catch (Exception ignore) {
                }
            } else {
                getStringListFromMap(excludes, type).add(field);
            }
        });
        return this;
    }

    public ResponseMessage<T> exclude(Collection<String> fields) {
        if (excludes == null) {
            excludes = new HashMap<>();
        }
        if (fields == null || fields.isEmpty()) {
            return this;
        }
        Class type;
        if (getResult() != null) {
            type = getResult().getClass();
        } else {
            return this;
        }
        exclude(type, fields);
        return this;
    }

    public ResponseMessage<T> include(Collection<String> fields) {
        if (includes == null) {
            includes = new HashMap<>();
        }
        if (fields == null || fields.isEmpty()) {
            return this;
        }
        Class type;
        if (getResult() != null) {
            type = getResult().getClass();
        } else {
            return this;
        }
        include(type, fields);
        return this;
    }

    public ResponseMessage<T> exclude(Class type, String... fields) {
        return exclude(type, Arrays.asList(fields));
    }

    public ResponseMessage<T> exclude(String... fields) {
        return exclude(Arrays.asList(fields));
    }

    public ResponseMessage<T> include(String... fields) {
        return include(Arrays.asList(fields));
    }

    protected Set<String> getStringListFromMap(Map<Class<?>, Set<String>> map, Class type) {
        return map.computeIfAbsent(type, k -> new HashSet<>());
    }

    @Override
    @SneakyThrows(JsonProcessingException.class)
    public String toString() {
        return JacksonUtils.getObjectMapper().writeValueAsString(this);
    }

    public Map<Class<?>, Set<String>> getExcludes() {
        return excludes;
    }

    public Map<Class<?>, Set<String>> getIncludes() {
        return includes;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void responseJson(HttpServletResponse response
            , int respondStatus) {
        response.setStatus(respondStatus);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String json = JacksonUtils.getObjectMapper().writeValueAsString(this);
            out.write(json);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (out != null)
                out.close();
        }
    }

}
