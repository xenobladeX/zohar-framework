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

import com.xenoblade.zohar.framework.commons.api.enums.IZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.enums.ZoharErrorCode;
import lombok.Getter;
import lombok.Setter;

/**
 * ZoharException
 * @author xenoblade
 * @since 1.0.0
 */
@Getter
@Setter
public class ZoharException extends RuntimeException{

    private static final long serialVersionUID = -5901014228840933934L;

    private String code;

    private Integer status;


    // TODO getHttpErrorCode
    public ZoharException(Throwable throwable) {
        super(throwable);
        this.errorCode(ZoharErrorCode.INNER_ERROR);
    }

    public ZoharException(String message) {
        this(message, ZoharErrorCode.INNER_ERROR);

    }

    public ZoharException(String message, IZoharErrorCode errorCode) {
        super(message);
        this.errorCode(errorCode);
    }

    public ZoharException(IZoharErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode(errorCode);
    }


    public ZoharException errorCode(IZoharErrorCode errorCode) {
        this.status = errorCode.getCode();
        this.code = errorCode.name();
        return this;
    }

    public ZoharException code(String code) {
        this.code = code;
        return this;
    }

    public ZoharException status(Integer status) {
        this.status = status;
        return this;
    }

    public ZoharException clearStackTrace() {
        this.setStackTrace(new StackTraceElement[]{});
        return this;
    }

}
