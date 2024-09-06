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
package org.zohar.framework.core.plugin;

import java.nio.file.Path;
import org.zohar.framework.core.plugin.api.PluginManager;
import org.zohar.framework.core.plugin.finder.ManifestPluginDescriptorFinder;
import org.zohar.framework.core.plugin.finder.PluginDescriptorFinder;
import org.zohar.framework.core.plugin.loader.CompoundPluginLoader;
import org.zohar.framework.core.plugin.loader.DevelopmentPluginLoader;
import org.zohar.framework.core.plugin.loader.JarPluginLoader;
import org.zohar.framework.core.plugin.loader.PluginLoader;

/**
 * It's a {@link PluginManager} that loads each plugin from a {@code jar} file.
 * Actually, a plugin is a fat jar, a jar which contains classes from all the libraries,
 * on which your project depends and, of course, the classes of current project.
 *
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public class JarPluginManager extends DefaultPluginManager {

    public JarPluginManager() {
        super();
    }

    public JarPluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    @Override
    protected PluginDescriptorFinder createPluginDescriptorFinder() {
        return new ManifestPluginDescriptorFinder();
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new CompoundPluginLoader()
                .add(new DevelopmentPluginLoader(this), this::isDevelopment)
                .add(new JarPluginLoader(this), this::isNotDevelopment);
    }

    @Override
    protected PluginRepository createPluginRepository() {
        return new CompoundPluginRepository()
                .add(new DevelopmentPluginRepository(getPluginsRoots()), this::isDevelopment)
                .add(new JarPluginRepository(getPluginsRoots()), this::isNotDevelopment);
    }

}