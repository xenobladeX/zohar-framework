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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zohar.framework.core.plugin.DefaultPluginManager;
import org.zohar.framework.core.plugin.api.extension.IEnum;
import org.zohar.framework.core.plugin.enumeration.DefaultEnumFactory;
import org.zohar.framework.core.plugin.test.TestEnum;

/**
 * @author xenoblade
 * @Date 2024/9/11
 * @since 0.0.1-SNAPSHOT
 */
public class DefaultCacheFilterExtensionFinderTest {

    private static final Logger log = LoggerFactory.getLogger(DefaultCacheFilterExtensionFinderTest.class);

    private DefaultPluginManager pluginManager;

    @TempDir
    Path pluginsPath;

    @BeforeEach
    public void setUp() throws IOException {
        pluginManager = new DefaultPluginManager(pluginsPath);
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
    }

    @Test
    public void testEnumExtension() {
        assertEquals(1,  pluginManager.getExtensionClasses(IEnum.class).size());
        assertEquals(TestEnum.TEST_ENUM_1, DefaultEnumFactory.INSTANCE.valueOf(0, IEnum.class));
    }

}