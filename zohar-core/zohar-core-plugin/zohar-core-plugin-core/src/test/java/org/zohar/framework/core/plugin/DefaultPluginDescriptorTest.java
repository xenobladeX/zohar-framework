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

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.zohar.framework.core.plugin.DefaultPluginDescriptor;
import org.zohar.framework.core.plugin.PluginDependency;

/**
 * @author Decebal Suiu
 * @Date 2024/8/29
 * @since 0.0.1-SNAPSHOT
 */
class DefaultPluginDescriptorTest {

    @Test
    void addDependency() {
        // Given a descriptor with empty dependencies
        DefaultPluginDescriptor descriptor = new DefaultPluginDescriptor();
        descriptor.setDependencies("");
        PluginDependency newDependency = new PluginDependency("test");

        // When I add a dependency
        descriptor.addDependency(newDependency);

        // Then the dependency is added
        List<PluginDependency> expected = new ArrayList<>();
        expected.add(newDependency);
        assertEquals(expected, descriptor.getDependencies());
    }
}