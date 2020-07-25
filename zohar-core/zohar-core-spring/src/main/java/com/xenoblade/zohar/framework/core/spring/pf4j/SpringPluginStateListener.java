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

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.ExtensionFinder;
import org.pf4j.ExtensionWrapper;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * SpringPluginStateListener
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class SpringPluginStateListener implements PluginStateListener {

    private ApplicationContext applicationContext;

    private SpringExtensionFactory springExtensionFactory;

    private ExtensionFinder extensionFinder;

    public SpringPluginStateListener(ApplicationContext applicationContext, ExtensionFinder extensionFinder) {
        this.applicationContext = applicationContext;
        this.extensionFinder = extensionFinder;
    }

    /**
     * Invoked when a plugin's state (for example DISABLED, STARTED) is changed.
     * @param event
     */
    @Override public void pluginStateChanged(PluginStateEvent event) {
        // TODO use SpringExtensionRegister
        switch (event.getPluginState()) {
            case STOPPED: {
                // get all bean names of plugin
//                springExtensionFactory.getAllExtensions(event.getPlugin().getPluginId()).stream().forEach(extension -> {
//                    String[] beanNames = applicationContext.getBeanNamesForType(extension.getClass());
//                    ((DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory()).destroySingleton(beanNames[0]);
//                });
                break;
            }
            case STARTED: {
                List<ExtensionWrapper> pluginExtensionWrappers = extensionFinder.find(event.getPlugin().getPluginId());
                for (ExtensionWrapper extensionWrapper: pluginExtensionWrappers) {
                    // Register an extension as bean
                    Object extension = extensionWrapper.getExtension();
                    if (extension != null) {
                        AbstractAutowireCapableBeanFactory beanFactory = (AbstractAutowireCapableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
                        beanFactory.registerSingleton(StrUtil.format("{}:{}", event.getPlugin().getPluginId(), extension.getClass().getName()), extension);
                    }
                }
            }
            default: {
                break;
            }
        }
    }
}
