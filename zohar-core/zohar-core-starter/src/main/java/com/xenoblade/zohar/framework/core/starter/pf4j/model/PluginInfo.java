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
package com.xenoblade.zohar.framework.core.starter.pf4j.model;

import com.xenoblade.zohar.framework.core.boot.pf4j.boot.PluginStartingError;
import lombok.Getter;
import org.pf4j.PluginDependency;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginState;

import java.util.ArrayList;
import java.util.List;

/**
 * PluginInfo
 * @author xenoblade
 * @since 1.0.0
 */
@Getter
public class PluginInfo implements PluginDescriptor {

    public String pluginId;

    public String pluginDescription;

    public String pluginClass;

    public String version;

    public String requires;

    public String provider;

    public String license;

    public List<PluginDependency> dependencies;

    public PluginState pluginState;

    public String newVersion;

    public boolean removed;

    public PluginStartingError startingError;

    public static PluginInfo build(PluginDescriptor descriptor,
                                   PluginState pluginState,
                                   String newVersion,
                                   PluginStartingError startingError,
                                   boolean removed) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.pluginId = descriptor.getPluginId();
        pluginInfo.pluginDescription = descriptor.getPluginDescription();
        pluginInfo.pluginClass = descriptor.getPluginClass();
        pluginInfo.version = descriptor.getVersion();
        pluginInfo.requires = descriptor.getRequires();
        pluginInfo.provider = descriptor.getProvider();
        pluginInfo.license = descriptor.getLicense();
        if (descriptor.getDependencies() != null) {
            pluginInfo.dependencies = new ArrayList<>(descriptor.getDependencies());
        }
        pluginInfo.pluginState = pluginState;
        pluginInfo.startingError = startingError;
        pluginInfo.newVersion = newVersion;
        pluginInfo.removed = removed;
        return pluginInfo;
    }
}
