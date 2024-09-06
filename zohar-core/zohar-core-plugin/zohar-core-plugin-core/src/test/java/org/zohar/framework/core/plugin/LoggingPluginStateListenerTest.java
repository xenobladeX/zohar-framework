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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zohar.framework.core.plugin.api.PluginManager;
import org.zohar.framework.core.plugin.api.model.PluginState;
import org.zohar.framework.core.plugin.api.model.PluginStateEvent;
import org.zohar.framework.core.plugin.api.model.PluginWrapper;

/**
 * @author Decebal Suiu
 * @Date 2024/9/3
 * @since 0.0.1-SNAPSHOT
 */
class LoggingPluginStateListenerTest {

    @Test
    void pluginStateChangedShouldLogStateChange() {
        Logger mockedLogger = mock(Logger.class);

        try (MockedStatic<LoggerFactory> context = Mockito.mockStatic(LoggerFactory.class)) {
            context.when(() -> LoggerFactory.getLogger(Mockito.any(Class.class)))
                    .thenReturn(mockedLogger);

            // create a PluginStateEvent
            PluginManager pluginManager = mock(PluginManager.class);
            PluginWrapper pluginWrapper = mock(PluginWrapper.class);
            when(pluginWrapper.getPluginId()).thenReturn("testPlugin");
            when(pluginWrapper.getPluginState()).thenReturn(PluginState.STARTED);
            PluginStateEvent event = new PluginStateEvent(pluginManager, pluginWrapper, PluginState.CREATED);

            // call the method under test
            LoggingPluginStateListener listener = new LoggingPluginStateListener();
            listener.pluginStateChanged(event);

            // verify that the logger was called with the expected message
            verify(mockedLogger).debug("The state of plugin '{}' has changed from '{}' to '{}'", "testPlugin", PluginState.CREATED, PluginState.STARTED);
        }
    }

}