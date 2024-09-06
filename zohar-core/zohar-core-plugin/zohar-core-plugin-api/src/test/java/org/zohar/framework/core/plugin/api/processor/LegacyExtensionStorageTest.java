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
package org.zohar.framework.core.plugin.api.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.zohar.framework.core.plugin.api.processor.LegacyExtensionStorage;

/**
 * @author Decebal Suiu
 * @Date 2024/9/3
 * @since 0.0.1-SNAPSHOT
 */
public class LegacyExtensionStorageTest {

    /**
     * Test of {@link LegacyExtensionStorage#read(Reader, Set)}.
     */
    @Test
    public void testRead() throws IOException {
        Reader reader = new StringReader(
                "# comment\n"
                        + "org.pf4j.demo.hello.HelloPlugin$HelloGreeting\n"
                        + "org.pf4j.demo.welcome.WelcomePlugin$WelcomeGreeting\n"
                        + "org.pf4j.demo.welcome.OtherGreeting\n");

        Set<String> entries = new HashSet<>();
        LegacyExtensionStorage.read(reader, entries);
        assertEquals(3, entries.size());
    }

}