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
package com.xenoblade.zohar.framework.tool;

import cn.hutool.crypto.SecureUtil;
import com.xenoblade.zohar.framework.tool.md5.MD5Util;
import org.junit.Assert;
import org.junit.Test;

/**
 * MD5Test
 * @author xenoblade
 * @since 1.0.0
 */
public class MD5Test {

    @Test
    public void testMd5() {
        String testStr = "Hello, world";
        long startTime = System.currentTimeMillis();
        String fastMd5 = MD5Util.computeMD5Hex(testStr.getBytes());
        long fastMd5EndTime = System.currentTimeMillis();
        String commonMd5 = SecureUtil.md5(testStr);
        long commonMd5EndTime = System.currentTimeMillis();
        Assert.assertTrue(fastMd5EndTime - startTime < commonMd5EndTime - fastMd5EndTime);
        Assert.assertEquals(commonMd5, fastMd5);
    }


}
