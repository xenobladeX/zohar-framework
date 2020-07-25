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

import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.commons.api.enums.BasicZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.pf4j.ExtensionFinder;
import org.pf4j.ExtensionWrapper;
import org.pf4j.PluginManager;

import java.util.List;

/**
 * ZoharExtensionsManager
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class ZoharExtensionsManager implements ExtensionsManager {

    protected final PluginManager pluginManager;

    public ZoharExtensionsManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public void inject() {
        pluginManager.loadPlugins();
        // get ExtensionFinder from pluginManager
        ExtensionFinder extensionFinder = null;
        try {
            extensionFinder = (ExtensionFinder)FieldUtils.readField(pluginManager, "extensionFinder", true);
        } catch (IllegalAccessException e) {
            throw new ZoharException(e,
                    StrUtil.format("Can't get filed extensionFinder from class {}",
                            pluginManager.getClass().getName()),
                    BasicZoharErrorCode.EXTENSIONS_INJECT_FAILED);
        }
        // add extensions from classpath
        String classPathPluginId = null;
        log.debug("Registering extensions from localPath");
        List<ExtensionWrapper> classPathExtensionWrappers = extensionFinder.find(classPathPluginId);
        for (ExtensionWrapper extensionWrapper: classPathExtensionWrappers) {
            handleExtension(extensionWrapper);
        }

        // start plugins
        pluginManager.startPlugins();
    }

    protected void handleExtension(ExtensionWrapper extensionWrapper) {


    }

}
