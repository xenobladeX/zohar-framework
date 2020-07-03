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

import lombok.Getter;
import org.pf4j.Extension;

/**
 * ExtendZoharErrorCode
 * @author xenoblade
 * @since 1.0.0
 */
@Extension
public enum BasicZoharErrorCode implements IZoharErrorCode {

    INVALID(0, "非法错误码"),
    ENUM_PARSE_ERROR(1, "枚举处理失败"),
    INVALID_PARAM(2, "参数错误"),
    JSON_FORMAT_ERROR(3, "JSON格式错误"),
    TYPE_MISMATCH(4, "类型不匹配"),
    METHOD_ARGUMENT_NOT_VALID(5, "方法参数不合法"),
    ENCRYPTION_ERROR(6, "加密错误"),
    DECRYPTION_ERROR(7, "解密错误"),
    API_DISCARD(8, "API接口已弃用"),
    FILE_READ_FAILED(9, "读取文件失败"),
    STREAM_READ_FAILED(10, "读流失败"),
    EXTENSIONS_INJECT_FAILED(11, "extensions 注入失败"),


    OK(200, "成功"),


    BAD_REQUEST(400, "错误的请求"),
    UNAUTHORIZED(401, "未授权"),


    NOT_FOUND(404, "找不到"),
    METHOD_NOT_ALLOWED(405, "不支持的请求方式"),
    NOT_ACCEPTABLE(406, "该请求不可接受"),


    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),


    INNER_ERROR(500, "内部错误"),

    SERVICE_UNAVAILABLE(503, "请求超时"),


    // datasource error
    DATA_NOT_EXIST(601, "数据不存在"),
    DATA_DUPLICATE(602, "数据重复"),
    DATA_SAVE_ERROR(603, "数据保存错误"),
    DATA_DELETE_ERROR(604, "数据删除错误"),
    DATA_UPDATE_ERROR(605, "数据更新错误"),
    DATA_ACCESS_ERROR(606, "数据访问错误");

    @Getter
    private Integer code;

    @Getter
    private String message;

    BasicZoharErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
