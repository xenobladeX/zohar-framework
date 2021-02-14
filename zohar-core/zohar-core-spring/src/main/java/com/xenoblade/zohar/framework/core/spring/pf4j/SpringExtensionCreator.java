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
import com.xenoblade.zohar.framework.core.common.pf4j.extension.factory.AbstractExtensionCreator;
import com.xenoblade.zohar.framework.core.spring.pf4j.annotation.ExtensionBean;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.GenericApplicationContext;

/**
 * SpringExtensionCreator
 * @author xenoblade
 * @since 1.0.0
 */
public class SpringExtensionCreator extends AbstractExtensionCreator {

    private SpringPluginManager pluginManager;

    public SpringExtensionCreator(SpringPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override public Boolean match(Class<?> extensionClass) {
        return extensionClass.isAnnotationPresent(ExtensionBean.class);
    }

    @Override public <T> T create(Class<T> extensionClass) {
        GenericApplicationContext pluginApplicationContext = getApplicationContext(extensionClass);
        if (pluginApplicationContext == null) {
            return null;
        }
        Object extension = null;
        try {
            extension = pluginApplicationContext.getBean(extensionClass);
        } catch (NoSuchBeanDefinitionException ignored) {} // do nothing
        if (extension == null) {
            // create extension
            Object extensionBean = super.create(extensionClass);
            // register bean to plugin applicationCOntext
            pluginApplicationContext.getBeanFactory().registerSingleton(
                    getExtensionBeanName(extensionClass), extensionBean);
            extension = extensionBean;
        }
        //noinspection unchecked
        return (T) extension;
    }

    @Override public Integer order() {
        return 100;
    }

    public String getExtensionBeanName(Class<?> extensionClass) {
        ExtensionBean extensionBean = extensionClass.getAnnotation(ExtensionBean.class);
        // ??? beanName 是否带上额外信息（pluginId）
        if (StrUtil.isNotEmpty(extensionBean.name())) {
            return extensionBean.name();
        } else {
            return extensionClass.getName();
        }
    }

    private GenericApplicationContext getApplicationContext(Class<?> extensionClass) {
        PluginWrapper pluginWrapper = pluginManager.whichPlugin(extensionClass);
        if (pluginWrapper == null) {
            if (getClass().getClassLoader() == extensionClass.getClassLoader()) {
                return (GenericApplicationContext)pluginManager.getMainApplicationContext();
            } else {
                return null;
            }
        } else {
            SpringPlugin plugin = (SpringPlugin) pluginWrapper.getPlugin();
            return plugin.getApplicationContext();
        }
    }


}
