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
package com.xenoblade.zohar.framework.core.api.dto;

import com.xenoblade.zohar.framework.core.extension.Extension;

/**
 * BasicErrorCode
 *
 * @author xenoblade
 * @since 0.0.1
 */
@Extension
public enum BasicErrorCode implements IZoharErrorCode {

    OK(100000, "OK"),
    INNER_ERROR(100001, "Inner Error"),
    FILE_READ_FAILED(100002, "File Read Failed"),
    JASON_PROCESS_ERROR(100003, "Jason Processed Error"),
    NOT_FOUND(100004, "Not Found"),
    METHOD_NOT_ALLOWED(100005, "Method Not Allowd"),
    MEDIA_TYPE_NOT_SUPPORTED(100006, "Media Type Not Supported"),
    MEDIA_TYPE_NOT_ACCEPTABLE(100007, "Media Type Not acceptable"),
    BAD_REQUEST(100008, "Bad Reqeust"),
    TYPE_MISMATCH(100009, "Type Mismatch"),
    SERVICE_UNAVAILABLE(100010, "Sevice Unavailable"),

    PARAM_MISS(100100, "Param is missed"),
    PARAM_NOT_VALID(100101, "Param Not Valid"),




    BASIC_MAX(100999, "Basic Error Max");


    private Integer code;

    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    BasicErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}