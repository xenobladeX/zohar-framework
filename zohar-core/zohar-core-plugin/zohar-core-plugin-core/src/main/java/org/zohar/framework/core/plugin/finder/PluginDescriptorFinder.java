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
package org.zohar.framework.core.plugin.finder;

import java.nio.file.Path;
import org.zohar.framework.core.plugin.PluginDescriptor;

/**
 * Find a plugin descriptor for a plugin path.
 * <p>
 * You can find the plugin descriptor in manifest file {@link ManifestPluginDescriptorFinder},
 * properties file {@link PropertiesPluginDescriptorFinder}, xml file,
 * java services (with {@link java.util.ServiceLoader}), etc.
 *
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public interface PluginDescriptorFinder {

    /**
     * Returns {@code true} if this finder is applicable to the given {@code pluginPath}.
     * This is used to select the appropriate finder for a given plugin path.
     *
     * @param pluginPath the plugin path
     */
    boolean isApplicable(Path pluginPath);

    /**
     * Find the plugin descriptor for the given {@code pluginPath}.
     *
     * @param pluginPath the plugin path
     * @return the plugin descriptor or {@code null} if not found
     */
    PluginDescriptor find(Path pluginPath);

}