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

import com.xenoblade.zohar.framework.commons.api.enums.BasicZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.enums.IZoharErrorCode;

/**
 * NotFoundException
 * @author xenoblade
 * @since 1.0.0
 */
public class NotFoundException extends ZoharException{

    private static final long serialVersionUID = 4335447221487758513L;

    public NotFoundException() {
        super(BasicZoharErrorCode.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, BasicZoharErrorCode.NOT_FOUND);
    }

    public NotFoundException(String message, IZoharErrorCode errorCode) {
        super(message, errorCode);
    }

    public NotFoundException(IZoharErrorCode errorCode) {
        super(errorCode);
    }

}
