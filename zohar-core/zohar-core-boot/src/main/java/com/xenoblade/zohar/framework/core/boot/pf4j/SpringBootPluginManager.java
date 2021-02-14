/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.core.boot.pf4j;

import com.xenoblade.zohar.framework.core.spring.pf4j.SpringExtensionFactory;
import com.xenoblade.zohar.framework.core.spring.pf4j.SpringPluginManager;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.ExtensionFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * PluginManager to hold the main Spring Boot ApplicationContext
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class SpringBootPluginManager extends SpringPluginManager {

    public Map<String, Object> presetProperties = new HashMap<>();

    public SpringBootPluginManager(Path pluginsRoot) {
        super(pluginsRoot);
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this);
    }

    // TODO custom createExtensionFinder

    public void presetProperties(Map<String, Object> presetProperties) {
        this.presetProperties.putAll(presetProperties);
    }

    public void presetProperties(String name, Object value) {
        this.presetProperties.put(name, value);
    }

    public Map<String, Object> getPresetProperties() {
        return presetProperties;
    }

}
