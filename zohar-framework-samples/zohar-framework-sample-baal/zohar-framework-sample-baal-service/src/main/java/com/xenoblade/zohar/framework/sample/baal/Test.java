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
package com.xenoblade.zohar.framework.sample.baal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * Test
 * @author xenoblade
 * @since 1.0.0
 */
@Component
@Slf4j
public class Test implements CommandLineRunner{

    @Autowired
    private ApplicationContext applicationContext;

    @Override public void run(String... args) {
        List<String> datasourceNames = Arrays.asList(applicationContext.getBeanNamesForType(DataSource.class));
        log.info("datasource names: {}", datasourceNames);
    }
}
