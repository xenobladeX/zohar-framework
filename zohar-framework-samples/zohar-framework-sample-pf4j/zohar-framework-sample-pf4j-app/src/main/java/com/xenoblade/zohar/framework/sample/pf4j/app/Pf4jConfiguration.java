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
package com.xenoblade.zohar.framework.sample.pf4j.app;

import com.xenoblade.zohar.framework.core.common.pf4j.ZoharExtensionsManager;
import com.xenoblade.zohar.framework.core.spring.pf4j.SpringExtensionsManager;
import com.xenoblade.zohar.framework.core.spring.pf4j.SpringPluginManager;
import com.xenoblade.zohar.framework.sample.pf4j.app.service.Greetings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Pf4jConfiguration
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
public class Pf4jConfiguration {

    @Bean
    public ZoharExtensionsManager zoharExtensionsManager() {
        SpringPluginManager pluginManager = new SpringPluginManager();
        return new SpringExtensionsManager(pluginManager);
    }

    @Bean
    @DependsOn("zoharExtensionsManager")
    public Greetings greetings() {
        return new Greetings();
    }

}
