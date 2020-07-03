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
package com.xenoblade.zohar.framework.core.common;

import com.xenoblade.zohar.framework.commons.api.enums.BasicZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.enums.IEnum;
import com.xenoblade.zohar.framework.commons.api.enums.IZoharErrorCode;
import com.xenoblade.zohar.framework.core.common.enums.AnotherZoharErrorCode;
import com.xenoblade.zohar.framework.core.common.pf4j.ZoharExtensionsManager;
import com.xenoblade.zohar.framework.core.common.pf4j.ZoharPluginManager;
import com.xenoblade.zohar.framework.core.common.pf4j.enums.ZoharEnumFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pf4j.PluginManager;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

/**
 * Pf4jTest
 * @author xenoblade
 * @since 1.0.0
 */
@RunWith(PowerMockRunner.class)
@Slf4j
public class Pf4jTest {

    private PluginManager pluginManager;

    @Before
    public void init() {
        pluginManager = new ZoharPluginManager();
        ZoharExtensionsManager zoharExtensionsInjector = new ZoharExtensionsManager(pluginManager);
        zoharExtensionsInjector.inject();
    }

    @Test
    public void testFindEnumClass() {
        List<Class<? extends IEnum>> zoharEnumClasses = pluginManager.getExtensionClasses(
                IEnum.class);
        Assert.assertEquals(zoharEnumClasses.size(), 2);
    }

    @Test
    public void testEnumValueOf() {

        Integer testValue = 1000;
        Assert.assertEquals(ZoharEnumFactory.INSTANCE.valueOf(testValue, IZoharErrorCode.class),
                AnotherZoharErrorCode.ZOHAR_ERROR_TEST);
        testValue = 200;
        Assert.assertEquals(ZoharEnumFactory.INSTANCE.valueOf(testValue, IZoharErrorCode.class),
                 BasicZoharErrorCode.OK);
    }

}
