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
package org.zohar.framework.core.extension.plugin;

import java.nio.file.Path;
import java.util.List;
import org.zohar.framework.core.extension.exception.PluginRuntimeException;

/**
 * Directory that contains plugins. A plugin could be a {@code directory}, {@code zip} or {@code jar} file.
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public interface PluginRepository {

    /**
     * List all plugin paths.
     *
     * @return a list with paths
     */
    List<Path> getPluginPaths();

    /**
     * Removes a plugin from the repository.
     *
     * @param pluginPath the plugin path
     * @return true if deleted
     * @throws PluginRuntimeException if something goes wrong
     */
    boolean deletePluginPath(Path pluginPath);

}