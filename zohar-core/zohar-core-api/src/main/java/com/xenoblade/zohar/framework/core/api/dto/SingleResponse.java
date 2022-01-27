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

import lombok.ToString;

/**
 * Response with single result
 *
 * @author xenoblade
 * @since 0.0.1
 */
@ToString(callSuper = true)
public class SingleResponse<T> extends Response {

    private static final long serialVersionUID = 7065529869144104549L;

    private T data;

    public static <T> SingleResponse<T> of(T data) {
        SingleResponse<T> response = SingleResponse.ok();
        response.setData(data);
        return response;
    }

    public static SingleResponse ok() {
        return SingleResponse.error(BasicErrorCode.OK);
    }

    public static SingleResponse error(IZoharErrorCode errorCodeEnum) {
        SingleResponse response = new SingleResponse();
        response.withErrorCodeEnum(errorCodeEnum);
        return response;
    }

    public static SingleResponse error(IZoharErrorCode errorCodeEnum, String errMessage) {
        SingleResponse response = SingleResponse.error(errorCodeEnum);
        response.setErrMessage(errMessage);
        return response;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}