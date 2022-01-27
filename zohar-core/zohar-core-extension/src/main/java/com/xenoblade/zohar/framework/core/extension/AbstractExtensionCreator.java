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
package com.xenoblade.zohar.framework.core.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractExtensionCreator
 *
 * @author xenoblade
 * @since 0.0.1
 */
public abstract class AbstractExtensionCreator implements ExtensionCreator {

    private static final Logger log = LoggerFactory.getLogger(AbstractExtensionCreator.class);

    @Override public Boolean match(Class<?> extensionClass) {
        return true;
    }

    @Override
    public <T> T create(Class<T> extensionClass) {
        log.debug("Create instance for extension '{}'", extensionClass.getName());
        try {
            return extensionClass.newInstance();
        } catch (Exception e) {
            throw new ExtensionRuntimeException(e);
        }
    }


}