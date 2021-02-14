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
package com.xenoblade.zohar.framework.core.boot.pf4j.internal;

import com.xenoblade.zohar.framework.core.boot.pf4j.SpringBootPlugin;
import com.xenoblade.zohar.framework.core.boot.pf4j.SpringBootPluginManager;
import com.xenoblade.zohar.framework.core.spring.pf4j.event.ZoharMainAppStartedEvent;
import org.pf4j.PluginState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * MainAppStartedListener
 * @author xenoblade
 * @since 1.0.0
 */
@Component
public class MainAppStartedListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private SpringBootPluginManager pluginManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (pluginManager.isAutoStartPlugin()) {
            pluginManager.startPlugins();
        }
        pluginManager.getPlugins(PluginState.STARTED).forEach(pluginWrapper -> {
            SpringBootPlugin springBootPlugin = (SpringBootPlugin) pluginWrapper.getPlugin();
            ApplicationContext pluginAppCtx = springBootPlugin.getApplicationContext();
            pluginAppCtx.publishEvent(new ZoharMainAppStartedEvent(applicationContext));
        });
        pluginManager.setMainApplicationStarted(true);
    }

}
