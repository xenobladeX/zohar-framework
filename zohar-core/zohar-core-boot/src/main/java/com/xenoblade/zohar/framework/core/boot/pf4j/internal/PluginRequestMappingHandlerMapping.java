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

import com.xenoblade.zohar.framework.core.boot.pf4j.boot.SpringBootstrap;
import com.xenoblade.zohar.framework.core.boot.pf4j.SpringBootPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PluginRequestMappingHandlerMapping
 * @author xenoblade
 * @since 1.0.0
 */
public class PluginRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    /**
     * {@inheritDoc}
     */
    @Override
    public void detectHandlerMethods(Object controller) {
        super.detectHandlerMethods(controller);
    }

    public void registerControllers(SpringBootPlugin springBootPlugin) {
        getControllerBeans(springBootPlugin).forEach(bean -> registerController(springBootPlugin, bean));
    }

    private void registerController(SpringBootPlugin springBootPlugin, Object controller) {
        String beanName = controller.getClass().getName();
        // unregister RequestMapping if already registered
        unregisterController(springBootPlugin, controller);
        springBootPlugin.registerBeanToMainContext(beanName, controller);
        detectHandlerMethods(controller);
    }

    public void unregisterControllers(SpringBootPlugin springBootPlugin) {
        getControllerBeans(springBootPlugin).forEach(bean -> unregisterController(springBootPlugin, bean));
    }

    public Set<Object> getControllerBeans(SpringBootPlugin springBootPlugin) {
        LinkedHashSet<Object> beans = new LinkedHashSet<>();
        ApplicationContext applicationContext = springBootPlugin.getApplicationContext();
        //noinspection unchecked
        // TODO 不要引入 spring boot 相关的 {SpringBootstrap}
        Set<String> sharedBeanNames = (Set<String>) applicationContext.getBean(
                SpringBootstrap.BEAN_IMPORTED_BEAN_NAMES);
        beans.addAll(applicationContext.getBeansWithAnnotation(Controller.class)
                .entrySet().stream().filter(beanEntry -> !sharedBeanNames.contains(beanEntry.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toList()));
        beans.addAll(applicationContext.getBeansWithAnnotation(RestController.class)
                .entrySet().stream().filter(beanEntry -> !sharedBeanNames.contains(beanEntry.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toList()));
        return beans;
    }

    private void unregisterController(SpringBootPlugin springBootPlugin, Object controller) {
        new HashMap<>(getHandlerMethods()).forEach((mapping, handlerMethod) -> {
            if (controller == handlerMethod.getBean()) {
                super.unregisterMapping(mapping);
            }
        });
        springBootPlugin.unregisterBeanFromMainContext(controller);
    }
}
