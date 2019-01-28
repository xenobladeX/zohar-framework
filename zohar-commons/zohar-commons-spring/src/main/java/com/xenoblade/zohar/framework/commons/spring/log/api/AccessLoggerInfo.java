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
package com.xenoblade.zohar.framework.commons.spring.log.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xenoblade.zohar.framework.commons.api.bean.Bean;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 访问日志信息,此对象包含了被拦截的方法和类信息,如果要对此对象进行序列化,请自行转换为想要的格式.
 * @author xenoblade
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessLoggerInfo implements Bean{

    private static final long serialVersionUID = 878023858711485931L;
    /**
     * 日志id
     */
    private String id;

    /**
     * 访问的操作
     *
     * @see AccessLogger#value()
     */
    private String action;

    /**
     * 描述
     *
     * @see AccessLogger#describe()
     */
    private String describe;

    /**
     * 访问对应的java方法
     */
    private Method method;

    /**
     * 访问对应的java类
     */
    private Class target;

    /**
     * 请求的参数,参数为java方法的参数而不是http参数,key为参数名,value为参数值.
     */
    private Map<String, Object> parameters;

    //    /**
    //     * 请求者ip地址
    //     */
    //    private String ip;
    //
    //    /**
    //     * 请求的url地址
    //     */
    //    private String url;
    //
    //    /**
    //     * http 请求头集合
    //     */
    //    private Map<String, String> httpHeaders;
    //
    //    /**
    //     * http 请求方法, GET,POST...
    //     */
    //    private String httpMethod;

    /**
     * 响应结果,方法的返回值
     */
    private Object response;

    /**
     * 上下文信息
     */
    private AccessLoggerContext context;

    /**
     * 请求时间戳
     *
     * @see System#currentTimeMillis()
     */
    private long requestTime;

    /**
     * 响应时间戳
     *
     * @see System#currentTimeMillis()
     */
    private long responseTime;

    /**
     * 异常信息,请求对应方法抛出的异常
     */
    @JsonSerialize(as = Exception.class, using = ExceptionSerizlizer.class)
    private Throwable exception;

    @JsonProperty("method")
    public String methodDescription() {
        StringJoiner methodAppender = new StringJoiner(",", method.getName().concat("("), ")");
        String[] parameterNames = parameters.keySet().toArray(new String[parameters.size()]);
        Class[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            methodAppender.add(parameterTypes[i].getSimpleName().concat(" ")
                    .concat(parameterNames.length > i ? parameterNames[i] : ("arg" + i)));
        }
        return methodAppender.toString();
    }

    @JsonProperty("useTime")
    public long useTime() {
        return responseTime - requestTime;
    }

}
