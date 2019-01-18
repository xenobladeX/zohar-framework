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
package com.xenoblade.zohar.framework.commons.api.exception;

import com.xenoblade.zohar.framework.commons.api.EErrorCode;
import lombok.Getter;


/**
 * ZoharException
 * @author xenoblade
 * @since 1.0.0
 */
public class ZoharException extends RuntimeException{

    private static final long serialVersionUID = -5901014228840933934L;

    @Getter
    private EErrorCode errorCode = EErrorCode.INVALID;

    public ZoharException(Throwable throwable) {
        super(throwable);
        this.errorCode = EErrorCode.INNER_ERROR;
    }

    public ZoharException(String message) {
        this(message, EErrorCode.INNER_ERROR);

    }

    public ZoharException(String message, int status) {
        this(message, EErrorCode.CodeOf(status));
    }

    public ZoharException(String message, EErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


    public ZoharException(String message, Throwable cause) {
        this(message, cause, EErrorCode.INNER_ERROR);
    }

    public ZoharException(String message, Throwable cause, int status) {
        this(message, cause, EErrorCode.CodeOf(status));
    }

    public ZoharException(String message, Throwable cause, EErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.name();
    }

    public Integer getStatus() {
        return errorCode.getCode();
    }


}
