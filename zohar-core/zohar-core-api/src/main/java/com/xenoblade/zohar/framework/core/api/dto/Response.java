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

import com.xenoblade.zohar.framework.core.extension.enumeration.ZoharEnumFactory;
import lombok.ToString;

/**
 * Response to client
 *
 * @author xenoblade
 * @since 0.0.1
 */
@ToString
public class Response extends DTO {

    private static final long serialVersionUID = -4834781873851458365L;

    private Integer errCode;

    private String errMessage;

    public static Response ok() {
        return Response.error(BasicErrorCode.OK);
    }

    public static Response error(IZoharErrorCode errorCodeEnum) {
        Response response = new Response();
        response.withErrorCodeEnum(errorCodeEnum);
        return response;
    }

    public static Response error(IZoharErrorCode errorCodeEnum, String errMessage) {
        Response response = Response.error(errorCodeEnum);
        if (errMessage != null) {
            response.setErrMessage(errMessage);
        }
        return response;
    }

    public void withErrorCodeEnum(IZoharErrorCode errorCodeEnum) {
        this.errCode = errorCodeEnum.getCode();
        this.errMessage = errorCodeEnum.getMessage();
    }

    public IZoharErrorCode calcErrorCodeEnum() {
        return ZoharEnumFactory.INSTANCE.valueOf(errCode, IZoharErrorCode.class);
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }
}