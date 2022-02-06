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
package com.xenoblade.zohar.framework.tool;

import com.xenoblade.zohar.framework.tool.bean.TestBigNumberBean;
import com.xenoblade.zohar.framework.tool.bean.TestDateBean;
import com.xenoblade.zohar.framework.tool.bean.TestNullBean;
import com.xenoblade.zohar.framework.tool.extension.TestJacksonUtilConfigurer;
import com.xenoblade.zohar.framework.tool.jackson.JacksonUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JacksonTest
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class JacksonTest {

    private Logger logger = LoggerFactory.getLogger(JacksonTest.class);

    @Test
    public void testCustomObjectMapper() {
        JacksonUtil.getObjectMapper();
        Assert.assertEquals(true, TestJacksonUtilConfigurer.isExecuted);
    }

    @Test
    public void testSerialNullBean() {
        TestNullBean testNullBean = new TestNullBean();
        String nullBeanJson = JacksonUtil.toJson(testNullBean);
        Assert.assertEquals("{\"aNotNull\":\"not null\"}", nullBeanJson);
    }

    @Test
    public void testSerialDateBean() {
        TestDateBean dateBean = new TestDateBean();
        String dateBeanJason = JacksonUtil.toJson(dateBean);
        Assert.assertEquals("{\"localDateTime\":\"2022-02-03 16:57:00\",\"date\":\"2022-02-03 16:57:00\"}", dateBeanJason);
    }

    @Test
    public void testSerialBigNumBean() {
        TestBigNumberBean bigNumberBean = new TestBigNumberBean();
        String bigNumBeanJson = JacksonUtil.toJson(bigNumberBean);
        Assert.assertEquals("{\"aBigDecimal\":\"3.12435\",\"aLong\":\"343254356884645753\",\"anotherLong\":\"4796904892307445488\"}", bigNumBeanJson);
    }


}