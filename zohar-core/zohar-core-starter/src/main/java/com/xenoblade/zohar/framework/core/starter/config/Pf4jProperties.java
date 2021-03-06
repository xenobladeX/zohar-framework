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
package com.xenoblade.zohar.framework.core.starter.config;

import lombok.Data;
import org.pf4j.PluginLoader;
import org.pf4j.RuntimeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pf4jProperties
 * @author xenoblade
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = Pf4jProperties.PREFIX)
@Data
public class Pf4jProperties {

    public static final String PREFIX = "zohar.pf4j";

    /**
     * Enable pf4j
     */
    private boolean enabled = false;
    /**
     * Auto start plugin when main app is ready
     */
    private boolean autoStartPlugin = true;
    /**
     * Plugins disabled by default
     */
    private String[] disabledPlugins;
    /**
     * Plugins enabled by default, prior to `disabledPlugins`
     */
    private String[] enabledPlugins;
    /**
     * Set to true to allow requires expression to be exactly x.y.z. The default is
     * false, meaning that using an exact version x.y.z will implicitly mean the
     * same as >=x.y.z
     */
    private boolean exactVersionAllowed = false;
    /**
     * Extended Plugin Class Directory
     */
    private List<String> classesDirectories = new ArrayList<>();
    /**
     * Extended Plugin Jar Directory
     */
    private List<String> libDirectories = new ArrayList<>();
    /**
     * Runtime Mode???development/deployment
     */
    private RuntimeMode runtimeMode = RuntimeMode.DEPLOYMENT;
    /**
     * Plugin root directory: default ???plugins???; when non-jar mode plugin, the value
     * should be an absolute directory address
     */
    private String pluginsRoot = "plugins";
    /**
     * Allows to provide custom plugin loaders
     */
    private Class<PluginLoader> customPluginLoader;
    /**
     * Profile for plugin Spring {@link ApplicationContext}
     */
    private String[] pluginProfiles = new String[] {"plugin"};
    /**
     * properties define under this property will be passed to
     * plugin `ApplicationContext` environment.
     */
    Map<String, Object> pluginProperties = new HashMap<>();
    /**
     * The system version used for comparisons to the plugin requires attribute.
     */
    private String systemVersion = "0.0.0";

}
