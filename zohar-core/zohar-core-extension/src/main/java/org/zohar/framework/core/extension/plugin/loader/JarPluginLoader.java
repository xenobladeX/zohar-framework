/*
 * Copyright [2024] [xenoblade]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zohar.framework.core.extension.plugin.loader;

import java.nio.file.Files;
import java.nio.file.Path;
import org.zohar.framework.core.extension.plugin.PluginDescriptor;
import org.zohar.framework.core.extension.plugin.PluginManager;
import org.zohar.framework.core.extension.util.file.FileUtils;

/**
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public class JarPluginLoader implements PluginLoader {

    protected PluginManager pluginManager;

    public JarPluginLoader(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public boolean isApplicable(Path pluginPath) {
        return Files.exists(pluginPath) && FileUtils.isJarFile(pluginPath);
    }

    @Override
    public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor) {
        PluginClassLoader pluginClassLoader = new PluginClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader());
        pluginClassLoader.addFile(pluginPath.toFile());

        return pluginClassLoader;
    }

}