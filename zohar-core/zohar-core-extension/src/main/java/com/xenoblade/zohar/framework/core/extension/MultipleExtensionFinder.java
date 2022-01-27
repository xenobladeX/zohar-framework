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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MultipleExtensionFinder
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class MultipleExtensionFinder implements ExtensionFinder {

    protected List<ExtensionFinder> finders = new ArrayList<>();


    @Override
    public <T> List<ExtensionWrapper<T>> find(Class<T> type) {
        List<ExtensionWrapper<T>> extensions = new ArrayList<>();
        for (ExtensionFinder finder : finders) {
            extensions.addAll(finder.find(type));
        }

        return extensions;
    }

    @Override
    public List<ExtensionWrapper> find() {
        List<ExtensionWrapper> extensions = new ArrayList<>();
        for (ExtensionFinder finder : finders) {
            extensions.addAll(finder.find());
        }

        return extensions;
    }

    @Override
    public Set<String> findClassNames() {
        Set<String> classNames = new HashSet<>();
        for (ExtensionFinder finder : finders) {
            classNames.addAll(finder.findClassNames());
        }

        return classNames;
    }

    public MultipleExtensionFinder addServiceProviderExtensionFinder(ExtensionFactory extensionFactory) {
        return add(new ServiceProviderExtensionFinder(extensionFactory));
    }

    public MultipleExtensionFinder add(ExtensionFinder finder) {
        finders.add(finder);

        return this;
    }

}