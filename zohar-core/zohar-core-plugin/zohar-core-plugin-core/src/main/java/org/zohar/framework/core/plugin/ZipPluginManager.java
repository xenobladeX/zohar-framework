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

import org.zohar.framework.core.plugin.finder.PluginDescriptorFinder;
import org.zohar.framework.core.plugin.finder.PropertiesPluginDescriptorFinder;
import org.zohar.framework.core.plugin.loader.CompoundPluginLoader;
import org.zohar.framework.core.plugin.loader.DefaultPluginLoader;
import org.zohar.framework.core.plugin.loader.DevelopmentPluginLoader;
import org.zohar.framework.core.plugin.loader.PluginLoader;

/**
 * It's a {@link PluginManager} that loads each plugin from a {@code zip} file.
 * The structure of the zip file is:
 * <ul>
 * <li>{@code lib} directory that contains all dependencies (as jar files); it's optional (no dependencies)
 * <li>{@code classes} directory that contains all plugin's classes
 * </ul>
 *  *
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public class ZipPluginManager extends DefaultPluginManager {

    @Override
    protected PluginDescriptorFinder createPluginDescriptorFinder() {
        return new PropertiesPluginDescriptorFinder();
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new CompoundPluginLoader()
                .add(new DevelopmentPluginLoader(this), this::isDevelopment)
                .add(new DefaultPluginLoader(this), this::isNotDevelopment);
    }

    @Override
    protected PluginRepository createPluginRepository() {
        return new CompoundPluginRepository()
                .add(new DevelopmentPluginRepository(getPluginsRoots()), this::isDevelopment)
                .add(new DefaultPluginRepository(getPluginsRoots()), this::isNotDevelopment);
    }

}