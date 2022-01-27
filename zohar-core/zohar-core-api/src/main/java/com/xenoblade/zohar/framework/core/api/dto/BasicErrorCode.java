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

    OK(200, "OK"),

    INNER_ERROR(500, "Inner Error");


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