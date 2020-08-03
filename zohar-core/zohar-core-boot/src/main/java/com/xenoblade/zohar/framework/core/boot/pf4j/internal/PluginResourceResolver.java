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

import com.xenoblade.zohar.framework.core.spring.utils.ApplicationContextProvider;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.Arrays;

/**
 * PluginResourceResolver
 * @author xenoblade
 * @since 1.0.0
 */
public class PluginResourceResolver extends PathResourceResolver {

    @Autowired
    @Lazy
    private PluginManager pluginManager;

    @Override
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        if (!(location instanceof ClassPathResource)) {
            return null;
        }
        ClassPathResource classPathLocation = (ClassPathResource) location;

        // pluginManager might not be autowired correctly because resolve bean
        // is instantiated before PluginManager.
        if (pluginManager == null) {
            pluginManager = ApplicationContextProvider.getBean(PluginManager.class);
        }

        for (PluginWrapper plugin : pluginManager.getPlugins(PluginState.STARTED)) {
            Resource pluginLocation = new ClassPathResource(classPathLocation.getPath(), plugin.getPluginClassLoader());
            Resource resource = pluginLocation.createRelative(resourcePath);
            if (resource.isReadable()) {
                if (checkResource(resource, pluginLocation)) {
                    return resource;
                }
                else if (logger.isWarnEnabled()) {
                    Resource[] allowedLocations = getAllowedLocations();
                    logger.warn("Resource path \"" + resourcePath + "\" was successfully resolved " +
                            "but resource \"" +	resource.getURL() + "\" is neither under the " +
                            "current location \"" + location.getURL() + "\" nor under any of the " +
                            "allowed locations " + (allowedLocations != null ? Arrays.asList(allowedLocations) : "[]"));
                }
            }
        }
        return null;
    }
}
