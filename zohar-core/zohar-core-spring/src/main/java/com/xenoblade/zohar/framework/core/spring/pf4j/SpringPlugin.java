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

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.core.spring.pf4j.annotation.ExtensionBean;
import com.xenoblade.zohar.framework.core.spring.pf4j.event.ZoharPluginRestartedEvent;
import com.xenoblade.zohar.framework.core.spring.pf4j.event.ZoharPluginStartedEvent;
import com.xenoblade.zohar.framework.core.spring.pf4j.event.ZoharPluginStoppedEvent;
import com.xenoblade.zohar.framework.core.spring.utils.ApplicationContextProvider;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Plugin;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.StopWatch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SpringPlugin
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class SpringPlugin extends Plugin {

    private GenericApplicationContext applicationContext;

    private final Set<String> injectedExtensionNames = new HashSet<>();

    private Boolean pluginApplicationStated = false;

    public SpringPlugin(PluginWrapper wrapper) {
        super(wrapper);
        applicationContext = createApplicationContext();
    }

    /**
     * Release plugin holding release on stop.
     */
    public void releaseResource() {
    }

    @Override public void start() {
        if (getWrapper().getPluginState() == PluginState.STARTED) {
            return;
        }
        log.debug("Starting plugin {} ......", getWrapper().getPluginId());
        StopWatch stopWatch = new StopWatch(StrUtil.format("Start plugin {}", getWrapper().getPluginId()));
        stopWatch.start("refresh applicationContext");
        startApplicationContext();
        stopWatch.stop();

        // register Extensions
        stopWatch.start("register extensions");
        List<Class<?>> extensionClasses = getWrapper().getPluginManager()
                .getExtensionClasses(getWrapper().getPluginId());
        for (Class<?> extensionClass : extensionClasses) {
            try {
                SpringExtensionFactory extensionFactory = (SpringExtensionFactory) getWrapper()
                        .getPluginManager().getExtensionFactory();
                // register to plugin applicationContext
                Object bean = extensionFactory.create(extensionClass);
                if (bean != null) {
                    // register to main applicationContext
                    ExtensionBean extensionBean = extensionClass.getAnnotation(ExtensionBean.class);
                    if (extensionBean != null && extensionBean.registerToMain()) {
                        String beanName = extensionFactory.getExtensionBeanName(extensionClass);
                        log.debug("Register extension <{}> to main ApplicationContext", extensionClass.getName());
                        registerBeanToMainContext(beanName, bean);
                        injectedExtensionNames.add(beanName);
                    }
                }
            } catch (Exception e) {
                stopWatch.stop();
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
        stopWatch.stop();

        ApplicationContextProvider.registerApplicationContext(applicationContext);
        stopWatch.start("publish ZoharPluginStartedEvent");
        applicationContext.publishEvent(new ZoharPluginStartedEvent(applicationContext));
        stopWatch.stop();
        if (getPluginManager().isMainApplicationStarted()) {
            // if main application context is not ready, don't send restart event
            stopWatch.start("publish ZoharPluginRestartedEvent");
            applicationContext.publishEvent(new ZoharPluginRestartedEvent(applicationContext));
            stopWatch.stop();
        }

        log.info(stopWatch.prettyPrint());
    }

    public void startApplicationContext() {
        applicationContext.refresh();
        // TODO register plugin request mapping
    }

    @Override public void stop() {
        if (getWrapper().getPluginState() != PluginState.STARTED) {
            return;
        }

        log.debug("Stopping plugin {} ......", getWrapper().getPluginId());
        StopWatch stopWatch = new StopWatch(StrUtil.format("Stop plugin {}", getWrapper().getPluginId()));
        releaseResource();
        stopWatch.start("unregister Extensions");
        // unregister Extensions
        for (String extensionName : injectedExtensionNames) {
            log.debug("Unregister extension <{}> to main ApplicationContext", extensionName);
            unregisterBeanFromMainContext(extensionName);
        }
        stopWatch.stop();

        // TODO unregister plugin request mapping
        stopWatch.start("publish ZoharPluginStoppedEvent");
        applicationContext.publishEvent(new ZoharPluginStoppedEvent(applicationContext));
        stopWatch.stop();
        injectedExtensionNames.clear();
        ApplicationContextProvider.unregisterApplicationContext(applicationContext);
        stopWatch.start("close applicationContext");
        ((ConfigurableApplicationContext) applicationContext).close();
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
    }


    protected abstract GenericApplicationContext createApplicationContext();

    public GenericApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public SpringPluginManager getPluginManager() {
        return (SpringPluginManager) getWrapper().getPluginManager();
    }

    public GenericApplicationContext getMainApplicationContext() {
        return (GenericApplicationContext) getPluginManager().getMainApplicationContext();
    }

    public void setApplicationContext(GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Boolean getPluginApplicationStated() {
        return pluginApplicationStated;
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
