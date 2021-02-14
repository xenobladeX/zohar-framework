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
package com.xenoblade.zohar.framework.core.spring.pf4j.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Pf4jProperties
 * @author xenoblade
 * @since 1.0.0
 */
@PropertySource(value = "classpath:application.properties")
@Data
@Configuration
public class Pf4jProperties implements Serializable {

    private static final long serialVersionUID = 6133846869263640750L;

    public static final String PREFIX = "zohar.pf4j";

    /**
     * Plugins disabled by default
     */
    @Value("${zohar.pf4j.disabledPlugins:}")
    private String[] disabledPlugins;
    /**
     * Plugins enabled by default, prior to `disabledPlugins`
     */
    @Value("${zohar.pf4j.enabledPlugins:}")
    private String[] enabledPlugins;
    /**
     * Set to true to allow requires expression to be exactly x.y.z. The default is
     * false, meaning that using an exact version x.y.z will implicitly mean the
     * same as >=x.y.z
     */
    @Value("${zohar.pf4j.exactVersionAllowed:false}")
    private String exactVersionAllowed;
    /**
     * Extended Plugin Class Directory
     */
    @Value("${zohar.pf4j.classesDirectories:}")
    private String[] classesDirectories;
    /**
     * Extended Plugin Jar Directory
     */
    @Value("${zohar.pf4j.libDirectories:}")
    private String[] libDirectories;
    /**
     * Runtime Mode：development/deployment
     */
    @Value("${zohar.pf4j.runtimeMode:development}")
    private String runtimeMode;
    /**
     * Plugin root directory: default “plugins”; when non-jar mode plugin, the value
     * should be an absolute directory address
     */
    @Value("${zohar.pf4j.pluginsRoot:plugins}")
    private String pluginsRoot;
    /**
     * Allows to provide custom plugin loaders
     */
    @Value("${zohar.pf4j.customPluginLoader:}")
    private String customPluginLoader;
    /**
     * Profile for plugin Spring {@link ApplicationContext}
     */
    @Value("${zohar.pf4j.pluginProfiles:plugin}")
    private String[] pluginProfiles;

    /**
     * The system version used for comparisons to the plugin requires attribute.
     */
    @Value("${zohar.pf4j.systemVersion:0.0.0}")
    private String systemVersion;

}
