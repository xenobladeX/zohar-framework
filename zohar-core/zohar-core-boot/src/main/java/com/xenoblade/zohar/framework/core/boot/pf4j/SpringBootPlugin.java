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
package com.xenoblade.zohar.framework.core.boot.pf4j;

import com.xenoblade.zohar.framework.core.boot.pf4j.boot.SpringBootstrap;
import com.xenoblade.zohar.framework.core.spring.pf4j.SpringPlugin;
import com.xenoblade.zohar.framework.core.boot.pf4j.internal.PluginRequestMappingHandlerMapping;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Base Pf4j Plugin for Spring Boot.
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class SpringBootPlugin extends SpringPlugin {

    private final SpringBootstrap springBootstrap;

    private final Set<String> injectedExtensionNames = new HashSet<>();

    public SpringBootPlugin(PluginWrapper wrapper) {
        super(wrapper);
        springBootstrap = createSpringBootstrap();
    }

    private PluginRequestMappingHandlerMapping getMainRequestMapping() {
        return (PluginRequestMappingHandlerMapping)
                getMainApplicationContext().getBean("requestMappingHandlerMapping");
    }

    @Override
    public void startApplicationContext() {
        setApplicationContext((GenericApplicationContext)springBootstrap.run());
        getMainRequestMapping().registerControllers(this);
    }

    protected abstract SpringBootstrap createSpringBootstrap();

    @Override
    protected GenericApplicationContext createApplicationContext() {
        return (GenericApplicationContext)getApplicationContext();
    }
}
