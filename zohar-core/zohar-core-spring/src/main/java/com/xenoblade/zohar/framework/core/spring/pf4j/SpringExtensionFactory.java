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

import com.xenoblade.zohar.framework.core.common.pf4j.extension.factory.ZoharExtensionFactory;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;

/**
 * SpringExtensionFactory
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class SpringExtensionFactory extends ZoharExtensionFactory {

    private PluginManager pluginManager;

    private boolean autowire;

    public SpringExtensionFactory(PluginManager pluginManager) {
        this(pluginManager, true);
    }

    public SpringExtensionFactory(PluginManager pluginManager, boolean autowire) {
        this.pluginManager = pluginManager;
        this.autowire = autowire;
    }

    @Override
    public <T> T create(Class<T> extensionClass) {
        T extension =  super.create(extensionClass);
        if (autowire && extension != null) {
            // test for SpringBean
            PluginWrapper pluginWrapper = pluginManager.whichPlugin(extensionClass);
            if (pluginWrapper != null) { // is plugin extension
                Plugin plugin = pluginWrapper.getPlugin();
                if (plugin instanceof SpringPlugin) {
                    // autowire
                    ApplicationContext pluginContext = ((SpringPlugin) plugin).getApplicationContext();
                    pluginContext.getAutowireCapableBeanFactory().autowireBean(extension);
                } else if (this.pluginManager instanceof SpringPluginManager) { // is system extension and plugin manager is SpringPluginManager
                    SpringPluginManager springPluginManager = (SpringPluginManager) this.pluginManager;
                    ApplicationContext pluginContext = springPluginManager.getApplicationContext();
                    pluginContext.getAutowireCapableBeanFactory().autowireBean(extension);
                }
            }
        }
        return extension;
    }

}
