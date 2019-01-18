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
package com.xenoblade.zohar.framework.commons.utils;

import com.xenoblade.zohar.framework.commons.utils.id.IDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * IDGeneratorTests
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class IDGeneratorTests {

    @Test
    public void test() {
        System.setProperty("id-worker","1");
        System.setProperty("id-datacenter","1");

        Assert.assertNotNull(IDGenerator.UUID.generate());
        Assert.assertNotNull(IDGenerator.MD5.generate());
        Assert.assertNotNull(IDGenerator.RANDOM.generate());
        Assert.assertNotNull(IDGenerator.SNOW_FLAKE.generate());
        Assert.assertNotNull(IDGenerator.SNOW_FLAKE_HEX.generate());

        for (int i = 0; i < 100; i++) {
            log.info("id: {}", IDGenerator.SNOW_FLAKE.generate());
        }
    }

}
