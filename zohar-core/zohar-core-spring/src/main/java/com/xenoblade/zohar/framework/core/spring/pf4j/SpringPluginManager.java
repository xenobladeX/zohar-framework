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

import com.xenoblade.zohar.framework.core.common.pf4j.ZoharPluginManager;
import org.pf4j.ExtensionFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

/**
 * SpringPluginManager
 * @author xenoblade
 * @since 1.0.0
 */
public class SpringPluginManager extends ZoharPluginManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        loadPlugins();
        startPlugins();

        AbstractAutowireCapableBeanFactory beanFactory = (AbstractAutowireCapableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        ExtensionsInjector extensionsInjector = new ExtensionsInjector(this, beanFactory);
        extensionsInjector.injectExtensions();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this);
    }
}
