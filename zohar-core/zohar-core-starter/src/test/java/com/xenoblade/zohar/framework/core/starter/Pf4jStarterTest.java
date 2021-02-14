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
package com.xenoblade.zohar.framework.core.starter;

import com.xenoblade.zohar.framework.commons.api.enums.BasicZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.enums.IZoharErrorCode;
import com.xenoblade.zohar.framework.core.common.pf4j.enums.ZoharEnumFactory;
import com.xenoblade.zohar.framework.core.starter.extension.AnotherZoharErrorCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Pf4jStarterTest
 * @author xenoblade
 * @since 1.0.0
 */
@SpringBootTest(classes = MainApplication.class)
@WebAppConfiguration
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@Slf4j
public class Pf4jStarterTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @Before
    @SneakyThrows
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Test
    @SneakyThrows
    public void testPluginController() {
        // main
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/main/info")).andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals(200, status);

        // plugin a
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/a/info")).andReturn();
        status = mvcResult.getResponse().getStatus();
        content = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals(200, status);

        // plugin b
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/b/info")).andReturn();
        status = mvcResult.getResponse().getStatus();
        content = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals(200, status);
    }

    @Test
    public void testEnumValueOf() {
        Integer testValue = 1000;
        Assert.assertEquals(ZoharEnumFactory.INSTANCE.valueOf(testValue, IZoharErrorCode.class),
                AnotherZoharErrorCode.ZOHAR_ERROR_TEST);
        testValue = 1100;
        Assert.assertEquals(ZoharEnumFactory.INSTANCE.valueOf(testValue, IZoharErrorCode.class).getCode(),
                testValue);
        testValue = 1200;
        Assert.assertEquals(ZoharEnumFactory.INSTANCE.valueOf(testValue, IZoharErrorCode.class).getCode(),
                testValue);
        testValue = 200;
        Assert.assertEquals(ZoharEnumFactory.INSTANCE.valueOf(testValue, IZoharErrorCode.class),
                BasicZoharErrorCode.OK);
    }


}
