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
package com.xenoblade.zohar.framework.core.starter.pf4j.controller;

import com.xenoblade.zohar.framework.core.boot.pf4j.SpringBootPluginManager;
import com.xenoblade.zohar.framework.core.starter.pf4j.model.PluginInfo;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PluginManagerController
 * @author xenoblade
 * @since 1.0.0
 */
@RestController
public class PluginManagerController {

    @Autowired
    private SpringBootPluginManager pluginManager;

    @GetMapping(value = "${zohar.pf4j.controller.base-path:/pf4j}/list")
    public List<PluginInfo> list() {
        List<PluginWrapper> loadedPlugins = pluginManager.getPlugins();

        // loaded plugins
        List<PluginInfo> plugins = loadedPlugins.stream().map(pluginWrapper -> {
            PluginDescriptor descriptor = pluginWrapper.getDescriptor();
            PluginDescriptor latestDescriptor = null;
            try {
                latestDescriptor = pluginManager.getPluginDescriptorFinder()
                        .find(pluginWrapper.getPluginPath());
            } catch (PluginRuntimeException ignored) {}
            String newVersion = null;
            if (latestDescriptor != null && !descriptor.getVersion().equals(latestDescriptor.getVersion())) {
                newVersion = latestDescriptor.getVersion();
            }

            return PluginInfo.build(descriptor,
                    pluginWrapper.getPluginState(), newVersion,
                    pluginManager.getPluginStartingError(pluginWrapper.getPluginId()),
                    latestDescriptor == null);
        }).collect(Collectors.toList());

        // yet not loaded plugins
        List<Path> pluginPaths = pluginManager.getPluginRepository().getPluginPaths();
        plugins.addAll(pluginPaths.stream().filter(path ->
                loadedPlugins.stream().noneMatch(plugin -> plugin.getPluginPath().equals(path))
        ).map(path -> {
            PluginDescriptor descriptor = pluginManager
                    .getPluginDescriptorFinder().find(path);
            return PluginInfo.build(descriptor, null, null, null, false);
        }).collect(Collectors.toList()));

        return plugins;
    }

    @PostMapping(value = "${zohar.pf4j.controller.base-path:/pf4j}/start/{pluginId}")
    public int start(@PathVariable String pluginId) {
        pluginManager.startPlugin(pluginId);
        return 0;
    }

    @PostMapping(value = "${zohar.pf4j.controller.base-path:/pf4j}/stop/{pluginId}")
    public int stop(@PathVariable String pluginId) {
        pluginManager.stopPlugin(pluginId);
        return 0;
    }

    @PostMapping(value = "${zohar.pf4j.controller.base-path:/pf4j}/reload/{pluginId}")
    public int reload(@PathVariable String pluginId) {
        PluginState pluginState = pluginManager.reloadPlugins(pluginId);
        return pluginState == PluginState.STARTED ? 0 : 1;
    }

    @PostMapping(value = "${zohar.pf4j.controller.base-path:/pf4j}/reload-all")
    public int reloadAll() {
        pluginManager.reloadPlugins(false );
        return 0;
    }

}
