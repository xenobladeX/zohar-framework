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
package com.xenoblade.zohar.framework.core.spring.pf4j;

import com.xenoblade.zohar.framework.commons.api.enums.BasicZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.enums.IZoharErrorCode;
import com.xenoblade.zohar.framework.core.common.pf4j.enums.ZoharEnumFactory;
import com.xenoblade.zohar.framework.core.spring.pf4j.config.Pf4jSpringTestConfiguration;
import com.xenoblade.zohar.framework.core.spring.pf4j.extension.AnotherZoharErrorCode;
import com.xenoblade.zohar.framework.sample.pf4j.spring.api.Greeting;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Pf4jSpringTest
 * @author xenoblade
 * @since 1.0.0
 */
@ContextConfiguration(classes = {Pf4jSpringTestConfiguration.class})
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
public class Pf4jSpringTest {

    @Autowired
    SpringPluginManager pluginManager;

    @Autowired
    private List<Greeting> greetings;

    @Test
    public void testPluginBeanRegisterToMain() {
        Assert.assertEquals(greetings.size(), 3);
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
