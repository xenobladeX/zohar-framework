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
package com.xenoblade.zohar.framework.core.spring.pf4j;

import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.core.common.pf4j.ZoharPluginManager;
import com.xenoblade.zohar.framework.core.spring.pf4j.event.ZoharPluginStateChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginRepository;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginState;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SpringPluginManager
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class SpringPluginManager extends ZoharPluginManager implements ApplicationContextAware {

    private ApplicationContext mainApplicationContext;

    private PluginRepository pluginRepository;

    private boolean autoStartPlugin = true;

    private boolean mainApplicationStarted;

    private String[] profiles;

    private final Map<String, PluginStartingError> startingErrors = new HashMap<>();

    public SpringPluginManager(Path pluginsRoot) {
        super(pluginsRoot);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.mainApplicationContext = applicationContext;
    }

    public ApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this);
    }

    @Override
    public PluginDescriptorFinder getPluginDescriptorFinder() {
        return super.getPluginDescriptorFinder();
    }

    @Override
    protected PluginRepository createPluginRepository() {
        this.pluginRepository = super.createPluginRepository();
        return this.pluginRepository;
    }

    public PluginRepository getPluginRepository() {
        return pluginRepository;
    }


    public void setAutoStartPlugin(boolean autoStartPlugin) {
        this.autoStartPlugin = autoStartPlugin;
    }

    public boolean isAutoStartPlugin() {
        return autoStartPlugin;
    }

    public void setMainApplicationStarted(boolean mainApplicationStarted) {
        this.mainApplicationStarted = mainApplicationStarted;
    }

    public boolean isMainApplicationStarted() {
        return mainApplicationStarted;
    }

    public void setProfiles(String[] profiles) {
        this.profiles = profiles;
    }

    public String[] getProfiles() {
        return profiles;
    }

    /**
     * This method load, start plugins and inject extensions in Spring
     */
    @PostConstruct
    public void init() {
        loadPlugins();

        // register main extension
        String mainPlugin = null;
        List<Class<?>> extensionClasses = getExtensionClasses(mainPlugin);
        for (Class extensionClass : extensionClasses) {
            log.debug("Register extension <{}> to main ApplicationContext", extensionClass);
            SpringExtensionFactory extensionFactory = (SpringExtensionFactory) getExtensionFactory();
            // register to main applicationContext
            extensionFactory.create(extensionClass);
        }
    }

    public PluginStartingError getPluginStartingError(String pluginId) {
        return startingErrors.get(pluginId);
    }

    private void doStartPlugins() {
        startingErrors.clear();
        StopWatch stopWatch = new StopWatch(
                StrUtil.format("start {} plugins: {}", resolvedPlugins.size(),
                        resolvedPlugins.stream().map(PluginWrapper::getPluginId)
                                .collect(Collectors.toList())));

        for (PluginWrapper pluginWrapper : resolvedPlugins) {
            PluginState pluginState = pluginWrapper.getPluginState();
            if ((PluginState.DISABLED != pluginState) && (PluginState.STARTED != pluginState)) {
                stopWatch.start(StrUtil.format("start plugin {}", pluginWrapper.getPluginId()));
                try {
                    pluginWrapper.getPlugin().start();
                    pluginWrapper.setPluginState(PluginState.STARTED);
                    startedPlugins.add(pluginWrapper);

                    firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    startingErrors.put(pluginWrapper.getPluginId(), PluginStartingError.of(
                            pluginWrapper.getPluginId(), e.getMessage(), e.toString()));
                } finally {
                    stopWatch.stop();
                }
            }
        }
        log.info(stopWatch.prettyPrint());
    }

    private void doStopPlugins() {
        startingErrors.clear();
        // stop started plugins in reverse order
        Collections.reverse(startedPlugins);
        Iterator<PluginWrapper> itr = startedPlugins.iterator();
        while (itr.hasNext()) {
            PluginWrapper pluginWrapper = itr.next();
            PluginState pluginState = pluginWrapper.getPluginState();
            if (PluginState.STARTED == pluginState) {
                try {
                    log.info("Stop plugin '{}'", getPluginLabel(pluginWrapper.getDescriptor()));
                    pluginWrapper.getPlugin().stop();
                    pluginWrapper.setPluginState(PluginState.STOPPED);
                    itr.remove();

                    firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));
                } catch (PluginRuntimeException e) {
                    log.error(e.getMessage(), e);
                    startingErrors.put(pluginWrapper.getPluginId(), PluginStartingError.of(
                            pluginWrapper.getPluginId(), e.getMessage(), e.toString()));
                }
            }
        }
    }

    private PluginState doStartPlugin(String pluginId, boolean sendEvent) {
        PluginWrapper plugin = getPlugin(pluginId);
        try {
            PluginState pluginState = super.startPlugin(pluginId);
            if (sendEvent) {
                mainApplicationContext.publishEvent(new ZoharPluginStateChangedEvent(mainApplicationContext));
            }
            return pluginState;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            startingErrors.put(plugin.getPluginId(), PluginStartingError.of(
                    plugin.getPluginId(), e.getMessage(), e.toString()));
        }
        return plugin.getPluginState();
    }

    private PluginState doStopPlugin(String pluginId, boolean sendEvent) {
        PluginWrapper plugin = getPlugin(pluginId);
        try {
            PluginState pluginState = super.stopPlugin(pluginId);
            if (sendEvent) {
                mainApplicationContext.publishEvent(new ZoharPluginStateChangedEvent(mainApplicationContext));
            }
            return pluginState;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            startingErrors.put(plugin.getPluginId(), PluginStartingError.of(
                    plugin.getPluginId(), e.getMessage(), e.toString()));
        }
        return plugin.getPluginState();
    }

    @Override
    public void startPlugins() {
        doStartPlugins();
        mainApplicationContext.publishEvent(new ZoharPluginStateChangedEvent(mainApplicationContext));
    }

    @Override
    public PluginState startPlugin(String pluginId) {
        return doStartPlugin(pluginId, true);
    }

    @Override
    public void stopPlugins() {
        doStopPlugins();
        mainApplicationContext.publishEvent(new ZoharPluginStateChangedEvent(mainApplicationContext));
    }

    @Override
    public PluginState stopPlugin(String pluginId) {
        return doStopPlugin(pluginId, true);
    }

    public void restartPlugins() {
        doStopPlugins();
        startPlugins();
    }

    public PluginState restartPlugin(String pluginId) {
        PluginState pluginState = doStopPlugin(pluginId, false);
        if (pluginState != PluginState.STARTED) {
            doStartPlugin(pluginId, false);
        }
        doStartPlugin(pluginId, false);
        mainApplicationContext.publishEvent(new ZoharPluginStateChangedEvent(mainApplicationContext));
        return pluginState;
    }

    public void reloadPlugins(boolean restartStartedOnly) {
        doStopPlugins();
        List<String> startedPluginIds = new ArrayList<>();
        getPlugins().forEach(plugin -> {
            if (plugin.getPluginState() == PluginState.STARTED) {
                startedPluginIds.add(plugin.getPluginId());
            }
            unloadPlugin(plugin.getPluginId());
        });
        loadPlugins();
        if (restartStartedOnly) {
            startedPluginIds.forEach(pluginId -> {
                // restart started plugin
                if (getPlugin(pluginId) != null) {
                    doStartPlugin(pluginId, false);
                }
            });
            mainApplicationContext.publishEvent(new ZoharPluginStateChangedEvent(mainApplicationContext));
        } else {
            startPlugins();
        }
    }

    public PluginState reloadPlugins(String pluginId) {
        PluginWrapper plugin = getPlugin(pluginId);
        doStopPlugin(pluginId, false);
        unloadPlugin(pluginId);
        try {
            loadPlugin(plugin.getPluginPath());
        } catch (Exception ex) {
            return null;
        }

        return doStartPlugin(pluginId, true);
    }

}
