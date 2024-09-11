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
package org.zohar.framework.core.plugin.finder.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zohar.framework.core.plugin.api.extension.IEnum;
import org.zohar.framework.core.plugin.enumeration.EnumFactory;
import org.zohar.framework.core.plugin.enumeration.DefaultEnumFactory;
import org.zohar.framework.core.plugin.finder.ExtensionWrapper;

/**
 * @author xenoblade
 * @since 0.0.1-SNAPSHOT
 */
public class EnumExtensionFinderFilter implements ExtensionFinderFilter {

    private static final Logger log = LoggerFactory.getLogger(EnumExtensionFinderFilter.class);

    private EnumFactory enumFactory = DefaultEnumFactory.INSTANCE;



    @Override
    public <T> Boolean match(ExtensionWrapper<T> extensionWrapper) {
        Class extensionClass = extensionWrapper.getDescriptor().extensionClass;
        return extensionClass.isEnum() && IEnum.class.isAssignableFrom(extensionClass);
    }

    @Override
    public <T> ExtensionWrapper<T> filter(ExtensionWrapper<T> extensionWrapper) {
        Class extensionClass = extensionWrapper.getDescriptor().extensionClass;
        enumFactory.resolve(extensionClass);
        return extensionWrapper;
    }
}