/*
 * Copyright [2022] [xenoblade]
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
package org.zohar.framework.core.plugin.creator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zohar.framework.core.plugin.api.extension.ExtensionFactory;
import org.zohar.framework.core.plugin.exception.PluginRuntimeException;

/**
 * The default implementation for {@link ExtensionFactory}.
 * It uses {@link Class#newInstance} method.
 *
 * @author Decebal Suiu
 * @since 0.0.1
 */
public class DefaultExtensionFactory implements ExtensionFactory {

    private static final Logger log = LoggerFactory.getLogger(DefaultExtensionFactory.class);

    /**
     * Creates an plugin instance.
     */
    @Override
    public <T> T create(Class<T> extensionClass) {
        log.debug("Create instance for plugin '{}'", extensionClass.getName());
        try {
            return extensionClass.newInstance();
        } catch (Exception e) {
            throw new PluginRuntimeException(e);
        }
    }

}