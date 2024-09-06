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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zohar.framework.core.plugin.DefaultPluginManager;
import org.zohar.framework.core.plugin.test.PluginZip;
import org.zohar.framework.core.plugin.util.file.FileUtils;

/**
 * @author Decebal Suiu
 * @Date 2024/9/3
 * @since 0.0.1-SNAPSHOT
 */
public class LoadPluginsFromMultipleRootsTest {

    private DefaultPluginManager pluginManager;

    Path pluginsPath1;
    Path pluginsPath2;

    @BeforeEach
    public void setUp() throws IOException {
        pluginsPath1 = Files.createTempDirectory("junit-pf4j-");
        pluginsPath2 = Files.createTempDirectory("junit-pf4j-");
        pluginManager = new DefaultPluginManager(pluginsPath1, pluginsPath2);
    }

    @AfterEach
    public void tearDown() throws IOException {
        FileUtils.delete(pluginsPath1);
        FileUtils.delete(pluginsPath2);
    }

    @Test
    public void load() throws Exception {
        PluginZip pluginZip1 = new PluginZip.Builder(pluginsPath1.resolve("my-plugin-1.2.3.zip"), "myPlugin")
                .pluginVersion("1.2.3")
                .build();

        PluginZip pluginZip2 = new PluginZip.Builder(pluginsPath2.resolve("my-other-plugin-4.5.6.zip"), "myOtherPlugin")
                .pluginVersion("4.5.6")
                .build();

        assertTrue(Files.exists(pluginZip1.path()));
        assertEquals(0, pluginManager.getPlugins().size());

        pluginManager.loadPlugins();

        assertTrue(Files.exists(pluginZip1.path()));
        assertTrue(Files.exists(pluginZip1.unzippedPath()));
        assertTrue(Files.exists(pluginZip2.path()));
        assertTrue(Files.exists(pluginZip2.unzippedPath()));
        assertEquals(2, pluginManager.getPlugins().size());
        assertEquals(pluginZip1.pluginId(), pluginManager.idForPath(pluginZip1.unzippedPath()));
        assertEquals(pluginZip2.pluginId(), pluginManager.idForPath(pluginZip2.unzippedPath()));
    }

}