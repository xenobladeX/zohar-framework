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
package com.xenoblade.zohar.framework.core.common.pf4j;

import com.xenoblade.zohar.framework.core.common.pf4j.extension.factory.ZoharExtensionFactory;
import com.xenoblade.zohar.framework.core.common.pf4j.extension.finder.DefaultExtensionFinder;
import com.xenoblade.zohar.framework.core.common.pf4j.extension.finder.ZoharExtensionFinder;
import com.xenoblade.zohar.framework.core.common.pf4j.extension.finder.ZoharEnumExtensionFinderFilter;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.pf4j.ExtensionFinder;

/**
 * ZoharPluginManager
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class ZoharPluginManager extends DefaultPluginManager {

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new ZoharExtensionFactory();
    }

    @Override
    protected ExtensionFinder createExtensionFinder() {
        ZoharExtensionFinder zoharExtensionFinder = new ZoharExtensionFinder(this);
        zoharExtensionFinder.addFilter(new ZoharEnumExtensionFinderFilter());
        DefaultExtensionFinder defaultExtensionFinder = new DefaultExtensionFinder(this);
        defaultExtensionFinder.add(zoharExtensionFinder);
        addPluginStateListener(zoharExtensionFinder);
        return defaultExtensionFinder;
    }

}
