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
package com.xenoblade.zohar.framework.commons.api;

import com.xenoblade.zohar.framework.commons.api.enums.IZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.enums.ZoharErrorCode;
import org.junit.Assert;
import org.junit.Test;


/**
 * ErrorCodeTests
 * @author xenoblade
 * @since 1.0.0
 */
public class ErrorCodeTests {

    @Test
    public void testErrorCode() {
        EErrorCode errorCode = EErrorCode.INNER_ERROR;
        Assert.assertEquals((long)errorCode.getCode(), 500);
    }


    @Test
    public void testCodeOf() {
        int code = 500;
        EErrorCode errorCode = EErrorCode.CodeOf(code);
        Assert.assertEquals(errorCode, EErrorCode.INNER_ERROR);
    }


}
