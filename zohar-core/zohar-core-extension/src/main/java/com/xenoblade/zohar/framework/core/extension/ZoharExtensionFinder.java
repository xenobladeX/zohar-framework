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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Find extensions with filters
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class ZoharExtensionFinder extends LegacyExtensionFinder {

    private List<ExtensionFinderFilter> filters = new ArrayList<>();

    public ZoharExtensionFinder(ExtensionFactory extensionFactory) {
        super(extensionFactory);
    }

    public ZoharExtensionFinder addFilter(ExtensionFinderFilter filter) {
        this.filters.add(filter);
        return this;
    }

    @Override public List<ExtensionWrapper> find() {
        return super.find().stream().map(tExtensionWrapper -> {
            for (ExtensionFinderFilter filter: filters) {
                if (filter.match(tExtensionWrapper)) {
                    tExtensionWrapper = filter.filter(tExtensionWrapper);
                }
            }
            return tExtensionWrapper;
        }).collect(Collectors.toList());
    }

    @Override public <T> List<ExtensionWrapper<T>> find(Class<T> type) {
        return super.find(type).stream().map(tExtensionWrapper -> {
            for (ExtensionFinderFilter filter: filters) {
                if (filter.match(tExtensionWrapper)) {
                    tExtensionWrapper = filter.filter(tExtensionWrapper);
                }
            }
            return tExtensionWrapper;
        }).collect(Collectors.toList());
    }

}