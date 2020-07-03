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
package com.xenoblade.zohar.framework.core.common.pf4j.extension.finder;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.ExtensionWrapper;
import org.pf4j.LegacyExtensionFinder;
import org.pf4j.PluginManager;
import org.pf4j.PluginStateEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ZoharExtensionFinder
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class ZoharExtensionFinder extends LegacyExtensionFinder {

    private List<ExtensionFinderFilter> filters = Lists.newArrayList();

    public ZoharExtensionFinder(PluginManager pluginManager) {
        super(pluginManager);
    }

    public ZoharExtensionFinder addFilter(ExtensionFinderFilter filter) {
        this.filters.add(filter);
        return this;
    }

    @Override public List<ExtensionWrapper> find(String pluginId) {
        return filterExtentionsWrappers(super.find(pluginId));
    }

    protected List<ExtensionWrapper> filterExtentionsWrappers(List<ExtensionWrapper> extensionWrappers) {
        return extensionWrappers.stream().map(tExtensionWrapper -> {
            for (ExtensionFinderFilter filter: filters) {
                if (filter.match(tExtensionWrapper)) {
                    tExtensionWrapper = filter.filter(tExtensionWrapper);
                }
            }
            return tExtensionWrapper;
        }).collect(Collectors.toList());
    }

    @Override
    public void pluginStateChanged(PluginStateEvent event) {
        // TODO update enumFactory
        super.pluginStateChanged(event);
    }
}
