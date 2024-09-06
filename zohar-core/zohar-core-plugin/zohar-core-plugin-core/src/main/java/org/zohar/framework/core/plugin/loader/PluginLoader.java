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
package org.zohar.framework.core.plugin.loader;

import java.nio.file.Path;
import org.zohar.framework.core.plugin.PluginDescriptor;

/**
 * Load all information (classes) needed by a plugin.
 * <p>
 * The plugin loader is responsible for creating a class loader for a plugin
 * and loading all classes/resources needed by the plugin.
 *
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public interface PluginLoader {

    /**
     * Returns {@code true} if this loader is applicable to the given plugin path.
     * This is used to select the appropriate loader for a given plugin path.
     *
     * @param pluginPath the plugin path
     * @return true if this loader is applicable to the given {@link Path}
     */
    boolean isApplicable(Path pluginPath);

    /**
     * Load all information (classes) needed by a plugin.
     *
     * @param pluginPath the plugin path
     * @param pluginDescriptor the plugin descriptor
     * @return the class loader for the plugin
     */
    ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor);

}