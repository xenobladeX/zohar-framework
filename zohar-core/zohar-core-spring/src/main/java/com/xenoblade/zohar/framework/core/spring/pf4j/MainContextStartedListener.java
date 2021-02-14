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

import com.xenoblade.zohar.framework.core.spring.pf4j.event.ZoharMainAppStartedEvent;
import org.pf4j.PluginState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * MainContextStartedListener
 * @author xenoblade
 * @since 1.0.0
 */
@Component
public class MainContextStartedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private SpringPluginManager pluginManager;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    @Override public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!pluginManager.isMainApplicationStarted()) {
            // start first time
            if (pluginManager.isAutoStartPlugin()) {
                pluginManager.reloadPlugins(false);
            }
            pluginManager.getPlugins(PluginState.STARTED).forEach(pluginWrapper -> {
                if (pluginWrapper.getPlugin() instanceof SpringPlugin) {
                    SpringPlugin springPlugin = (SpringPlugin) pluginWrapper.getPlugin();
                    ApplicationContext pluginAppCtx = springPlugin.getApplicationContext();
                    pluginAppCtx.publishEvent(new ZoharMainAppStartedEvent(applicationContext));
                }
            });
            pluginManager.setMainApplicationStarted(true);
        } else {
            // TODO refresh plugin?

        }

    }
}
