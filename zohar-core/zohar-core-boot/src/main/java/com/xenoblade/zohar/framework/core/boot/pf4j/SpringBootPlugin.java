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

import cn.hutool.core.lang.Assert;
import com.xenoblade.zohar.framework.core.boot.pf4j.boot.SpringBootstrap;
import com.xenoblade.zohar.framework.core.boot.pf4j.boot.ZoharPluginRestartedEvent;
import com.xenoblade.zohar.framework.core.boot.pf4j.boot.ZoharPluginStartedEvent;
import com.xenoblade.zohar.framework.core.boot.pf4j.boot.ZoharPluginStoppedEvent;
import com.xenoblade.zohar.framework.core.boot.pf4j.internal.PluginRequestMappingHandlerMapping;
import com.xenoblade.zohar.framework.core.boot.pf4j.internal.SpringExtensionFactory;
import com.xenoblade.zohar.framework.core.spring.utils.ApplicationContextProvider;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Plugin;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Set;

/**
 * Base Pf4j Plugin for Spring Boot.
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class SpringBootPlugin extends Plugin {

    private final SpringBootstrap springBootstrap;

    private ApplicationContext applicationContext;

    public SpringBootPlugin(PluginWrapper wrapper) {
        super(wrapper);
        springBootstrap = createSpringBootstrap();
    }

    private PluginRequestMappingHandlerMapping getMainRequestMapping() {
        return (PluginRequestMappingHandlerMapping)
                getMainApplicationContext().getBean("requestMappingHandlerMapping");
    }

    /**
     * Release plugin holding release on stop.
     */
    public void releaseResource() {
    }

    @Override
    public void start() {
        if (getWrapper().getPluginState() == PluginState.STARTED) {
            return;
        }

        long startTs = System.currentTimeMillis();
        log.debug("Starting plugin {} ......", getWrapper().getPluginId());

        applicationContext = springBootstrap.run();
        getMainRequestMapping().registerControllers(this);

        // register Extensions
        Set<String> extensionClassNames = getWrapper().getPluginManager()
                .getExtensionClassNames(getWrapper().getPluginId());
        for (String extensionClassName : extensionClassNames) {
            try {
                log.debug("Register extension <{}> to main ApplicationContext", extensionClassName);
                Class<?> extensionClass = getWrapper().getPluginClassLoader().loadClass(extensionClassName);
                SpringExtensionFactory extensionFactory = (SpringExtensionFactory) getWrapper()
                        .getPluginManager().getExtensionFactory();
                Object bean = extensionFactory.create(extensionClass);
                String beanName = extensionFactory.getExtensionBeanName(extensionClass);
                registerBeanToMainContext(beanName, bean);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }

        ApplicationContextProvider.registerApplicationContext(applicationContext);
        applicationContext.publishEvent(new ZoharPluginStartedEvent(applicationContext));
        if (getPluginManager().isMainApplicationStarted()) {
            // if main application context is not ready, don't send restart event
            applicationContext.publishEvent(new ZoharPluginRestartedEvent(applicationContext));
        }

        log.debug("Plugin {} is started in {}ms", getWrapper().getPluginId(), System.currentTimeMillis() - startTs);
    }

    @Override
    public void stop() {
        if (getWrapper().getPluginState() != PluginState.STARTED) {
            return;
        }

        log.debug("Stopping plugin {} ......", getWrapper().getPluginId());
        releaseResource();
        // register Extensions
        Set<String> extensionClassNames = getWrapper().getPluginManager()
                .getExtensionClassNames(getWrapper().getPluginId());
        for (String extensionClassName : extensionClassNames) {
            try {
                log.debug("Register extension <{}> to main ApplicationContext", extensionClassName);
                Class<?> extensionClass = getWrapper().getPluginClassLoader().loadClass(extensionClassName);
                SpringExtensionFactory extensionFactory = (SpringExtensionFactory) getWrapper()
                        .getPluginManager().getExtensionFactory();
                String beanName = extensionFactory.getExtensionBeanName(extensionClass);
                unregisterBeanFromMainContext(beanName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }

        getMainRequestMapping().unregisterControllers(this);
        applicationContext.publishEvent(new ZoharPluginStoppedEvent(applicationContext));
        ApplicationContextProvider.unregisterApplicationContext(applicationContext);
        ((ConfigurableApplicationContext) applicationContext).close();

        log.debug("Plugin {} is stopped", getWrapper().getPluginId());
    }

    protected abstract SpringBootstrap createSpringBootstrap();

    public GenericApplicationContext getApplicationContext() {
        return (GenericApplicationContext) applicationContext;
    }

    public SpringBootPluginManager getPluginManager() {
        return (SpringBootPluginManager) getWrapper().getPluginManager();
    }

    public GenericApplicationContext getMainApplicationContext() {
        return (GenericApplicationContext) getPluginManager().getMainApplicationContext();
    }

    public void registerBeanToMainContext(String beanName, Object bean) {
        Assert.notNull(bean, "bean must not be null");
        beanName = StringUtils.isEmpty(beanName) ? bean.getClass().getName() : beanName;
        getMainApplicationContext().getBeanFactory().registerSingleton(beanName, bean);
    }

    public void unregisterBeanFromMainContext(String beanName) {
        Assert.notNull(beanName, "bean must not be null");
        ((AbstractAutowireCapableBeanFactory) getMainApplicationContext().getBeanFactory())
                .destroySingleton(beanName);
    }

    public void unregisterBeanFromMainContext(Object bean) {
        Assert.notNull(bean, "bean must not be null");
        String beanName = bean.getClass().getName();
        ((AbstractAutowireCapableBeanFactory) getMainApplicationContext().getBeanFactory())
                .destroySingleton(beanName);
    }

}
