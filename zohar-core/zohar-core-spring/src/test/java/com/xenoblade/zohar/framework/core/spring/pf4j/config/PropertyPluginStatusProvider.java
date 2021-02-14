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

import org.pf4j.PluginStatusProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PropertyPluginStatusProvider
 * @author xenoblade
 * @since 1.0.0
 */
public class PropertyPluginStatusProvider implements PluginStatusProvider {

    private List<String> enabledPlugins;
    private List<String> disabledPlugins;

    public PropertyPluginStatusProvider(Pf4jProperties pf4jProperties) {
        this.enabledPlugins = pf4jProperties.getEnabledPlugins() != null
                ? Arrays.asList(pf4jProperties.getEnabledPlugins()) : new ArrayList<>();
        this.disabledPlugins = pf4jProperties.getDisabledPlugins() != null
                ? Arrays.asList(pf4jProperties.getDisabledPlugins()) : new ArrayList<>();
    }

    public static boolean isPropertySet(Pf4jProperties pf4jProperties) {
        return pf4jProperties.getEnabledPlugins() != null && pf4jProperties.getEnabledPlugins().length > 0
                || pf4jProperties.getDisabledPlugins() != null && pf4jProperties.getDisabledPlugins().length > 0;
    }

    @Override
    public boolean isPluginDisabled(String pluginId) {
        if (disabledPlugins.contains(pluginId)) {
            return true;
        }
        return !enabledPlugins.isEmpty() && !enabledPlugins.contains(pluginId);
    }

    @Override
    public void disablePlugin(String pluginId) {
        if (isPluginDisabled(pluginId)) {
            return;
        }
        disabledPlugins.add(pluginId);
        enabledPlugins.remove(pluginId);
    }

    @Override
    public void enablePlugin(String pluginId) {
        if (!isPluginDisabled(pluginId)) {
            return;
        }
        enabledPlugins.add(pluginId);
        disabledPlugins.remove(pluginId);
    }
}
