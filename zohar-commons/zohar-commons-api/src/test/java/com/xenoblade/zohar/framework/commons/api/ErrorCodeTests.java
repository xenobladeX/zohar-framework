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
import com.xenoblade.zohar.framework.commons.api.enums.dynamic.DigitsDynamicEnum;
import com.xenoblade.zohar.framework.commons.api.enums.dynamic.DigitsExtendDynamicEnum;
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


    @Test
    public void testDirectAccess() {
        Assert.assertEquals(0, DigitsDynamicEnum.ZERO.ordinal());
    }

    @Test
    // valueOf() belongs to DynaEnum. I suppressed warning by annotation.
    // Other solution is to implement trivial valueOf() that just calls parent's implementation in DigitsDynaEnum.
    @SuppressWarnings("static-access")
    public void testValueOf() {
        Assert.assertEquals(1, DigitsDynamicEnum.valueOf(DigitsDynamicEnum.class, "ONE").ordinal());
    }

    @Test
    public void testValues() {
        DigitsDynamicEnum[] values = (DigitsDynamicEnum[]) DigitsExtendDynamicEnum.values();
        Assert.assertEquals(8, values.length);
//        Assert.assertArrayEquals(new DigitsDynamicEnum[] {DigitsDynamicEnum.ZERO, DigitsDynamicEnum.ONE, DigitsDynamicEnum.TWO, DigitsDynamicEnum.THREE}, values);
    }

    @SuppressWarnings("static-access")
    @Test
    public void testEquals() {
        Assert.assertTrue(DigitsDynamicEnum.ONE == DigitsDynamicEnum.ONE);
        Assert.assertTrue(DigitsDynamicEnum.ONE != DigitsDynamicEnum.TWO);

        Assert.assertEquals(DigitsDynamicEnum.ONE, DigitsDynamicEnum.ONE);
        Assert.assertEquals(DigitsDynamicEnum.ONE, DigitsDynamicEnum.valueOf(DigitsDynamicEnum.class, "ONE"));
    }

}
