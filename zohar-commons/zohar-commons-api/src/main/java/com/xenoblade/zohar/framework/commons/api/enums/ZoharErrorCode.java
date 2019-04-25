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
package com.xenoblade.zohar.framework.commons.api.enums;

import com.sun.org.apache.bcel.internal.generic.ClassGenException;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ZoharErrorCode
 * @author xenoblade
 * @since 1.0.0
 */
// TODO: 为了使用 message，将ZoharErrorCode移动到 zohar-commons-spring
public enum ZoharErrorCode implements IZoharErrorCode{

    INVALID("非法错误码"),
    ENUM_PARSE_ERROR("枚举处理失败"),
    INVALID_PARAM("参数错误"),
    JSON_FORMAT_ERROR("JSON格式错误"),
    TYPE_MISMATCH("类型不匹配"),
    METHOD_ARGUMENT_NOT_VALID("方法参数不合法"),
    ENCRYPTION_ERROR("加密错误"),
    DECRYPTION_ERROR("解密错误"),
    API_DISCARD("API接口已弃用"),


    OK(200, "成功"),


    BAD_REQUEST(400, "错误的请求"),
    UNAUTHORIZED(401, "未授权"),


    NOT_FOUND(404, "找不到"),
    METHOD_NOT_ALLOWED(405, "不支持的请求方式"),
    NOT_ACCEPTABLE(406, "该请求不可接受"),


    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),


    INNER_ERROR(500, "内部错误"),

    SERVICE_UNAVAILABLE(503, "请求超时"),




    MAX_FRAMEWORK_ERROR(600, "zohar-framework最大错误码，请不要直接使用");

    @Getter
    private Integer code;

    @Getter
    private String message;

    private static Map<Integer, ZoharErrorCode> map = new HashMap<>();

    static {
        int code = 0;
        Boolean isFirst = true;
        for (ZoharErrorCode errorCode : ZoharErrorCode.values()) {
            if (errorCode.code != null) {
                if (code >= errorCode.code) {
                    throw new ClassGenException("ZoharErrorCode中的code发生冲突");
                }
                code = errorCode.code;
            } else if (isFirst){
                errorCode.code = code;
                isFirst = false;
            } else {
                errorCode.code = ++code;
            }
            map.put(errorCode.code, errorCode);
        }
    }


    public static ZoharErrorCode CodeOf(Integer code) throws ZoharException {
        ZoharErrorCode errorCode = (ZoharErrorCode) map.get(code);
        return Optional.ofNullable(errorCode).orElseThrow(() ->
                new ZoharException(String.format("code %d转ZoharException失败", code), ZoharErrorCode.ENUM_PARSE_ERROR)
        );
    }


    ZoharErrorCode(String message) {
        this.message = message;

    }

    ZoharErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
