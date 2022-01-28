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
package com.xenoblade.zohar.framework.core.api.exception;

import com.xenoblade.zohar.framework.core.api.dto.BasicErrorCode;
import com.xenoblade.zohar.framework.core.api.dto.IZoharErrorCode;

/**
 * Parent exception of all exceptions
 *
 * @author xenoblade
 * @since 0.0.1
 */
public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = -9215755065659723531L;

    private IZoharErrorCode errCode = BasicErrorCode.INNER_ERROR;

    public BaseException(IZoharErrorCode errCodeEnum) {
        super(errCodeEnum.getMessage());
        this.errCode = errCodeEnum;
    }

    public BaseException(IZoharErrorCode errCodeEnum, String errMessage) {
        super(errMessage);
        this.errCode = errCodeEnum;
    }

    public BaseException(IZoharErrorCode errCodeEnum, Throwable e) {
        super(errCodeEnum.getMessage(), e);
        this.errCode = errCodeEnum;
    }

    public BaseException(IZoharErrorCode errCodeEnum, String errMessage, Throwable e) {
        super(errMessage, e);
        this.errCode = errCodeEnum;
    }

    public IZoharErrorCode getErrCode() {
        return errCode;
    }

    public void setErrCode(IZoharErrorCode errCode) {
        this.errCode = errCode;
    }
}