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
import lombok.extern.slf4j.Slf4j;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Pf4j ExtensionFactory to create/retrieve extension bean from spring
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class SpringExtensionFactory implements ExtensionFactory {

    private SpringBootPluginManager pluginManager;

    public SpringExtensionFactory(SpringBootPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public <T> T create(Class<T> extensionClass) {
        GenericApplicationContext pluginApplicationContext = getApplicationContext(extensionClass);
        Object extension = null;
        try {
            extension = pluginApplicationContext.getBean(extensionClass);
        } catch (NoSuchBeanDefinitionException ignored) {} // do nothing
        if (extension == null) {
            Object extensionBean = createWithoutSpring(extensionClass);
            pluginApplicationContext.getBeanFactory().registerSingleton(
                    extensionClass.getName(), extensionBean);
            extension = extensionBean;
        }
        //noinspection unchecked
        return (T) extension;
    }

    public String getExtensionBeanName(Class<?> extensionClass) {
        String[] beanNames = getApplicationContext(extensionClass)
                .getBeanNamesForType(extensionClass);
        return beanNames.length > 0 ? beanNames[0] : null;
    }

    private Object createWithoutSpring(Class<?> extensionClass) {
        try {
            return extensionClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private GenericApplicationContext getApplicationContext(Class<?> extensionClass) {
        PluginWrapper pluginWrapper = pluginManager.whichPlugin(extensionClass);
        SpringBootPlugin plugin = (SpringBootPlugin) pluginWrapper.getPlugin();
        return plugin.getApplicationContext();
    }
}
