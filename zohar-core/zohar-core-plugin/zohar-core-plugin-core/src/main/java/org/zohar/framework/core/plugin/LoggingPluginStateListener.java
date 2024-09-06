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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zohar.framework.core.plugin.api.model.PluginStateEvent;
import org.zohar.framework.core.plugin.api.PluginStateListener;

/**
 * It's an implementation of {@link PluginStateListener} that writes all events to logger (DEBUG level).
 * This listener is added automatically by {@link DefaultPluginManager} for {@code dev} mode.
 *
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public class LoggingPluginStateListener implements PluginStateListener {

    private static final Logger log = LoggerFactory.getLogger(LoggingPluginStateListener.class);

    @Override
    public void pluginStateChanged(PluginStateEvent event) {
        log.debug("The state of plugin '{}' has changed from '{}' to '{}'", event.getPlugin().getPluginId(),
                event.getOldState(), event.getPluginState());
    }

}