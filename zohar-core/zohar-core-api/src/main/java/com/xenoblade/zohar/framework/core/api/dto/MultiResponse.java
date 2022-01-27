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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Response with batch record
 *
 * @author xenoblade
 * @since 0.0.1
 */
@ToString(callSuper = true)
public class MultiResponse<T> extends Response {

    private static final long serialVersionUID = -7989927884580907576L;

    private Collection<T> data;

    public static <T> MultiResponse<T> of(Collection<T> data) {
        MultiResponse<T> response = MultiResponse.ok();
        response.setData(data);
        return response;
    }

    public static MultiResponse ok() {
        return MultiResponse.error(BasicErrorCode.OK);
    }

    public static MultiResponse error(IZoharErrorCode errorCodeEnum) {
        MultiResponse response = new MultiResponse();
        response.withErrorCodeEnum(errorCodeEnum);
        return response;
    }

    public static MultiResponse error(IZoharErrorCode errorCodeEnum, String errMessage) {
        MultiResponse response = MultiResponse.error(errorCodeEnum);
        response.setErrMessage(errMessage);
        return response;
    }

    public Collection<T> getData() {
        return null == data ? Collections.emptyList() : new ArrayList<>(data);
    }

    public void setData(Collection<T> data) {
        this.data = data;
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }


}