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
package org.zohar.framework.core.extension.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zohar.framework.core.extension.plugin.creator.ExtensionFactory;
import org.zohar.framework.core.extension.exception.PluginRuntimeException;
import org.zohar.framework.core.extension.plugin.creator.DefaultExtensionFactory;
import org.zohar.framework.core.extension.plugin.finder.CompoundPluginDescriptorFinder;
import org.zohar.framework.core.extension.plugin.finder.DefaultExtensionFinder;
import org.zohar.framework.core.extension.plugin.finder.ExtensionFinder;
import org.zohar.framework.core.extension.plugin.finder.ManifestPluginDescriptorFinder;
import org.zohar.framework.core.extension.plugin.finder.PluginDescriptorFinder;
import org.zohar.framework.core.extension.plugin.finder.PropertiesPluginDescriptorFinder;
import org.zohar.framework.core.extension.plugin.loader.CompoundPluginLoader;
import org.zohar.framework.core.extension.plugin.loader.DefaultPluginLoader;
import org.zohar.framework.core.extension.plugin.loader.DevelopmentPluginLoader;
import org.zohar.framework.core.extension.plugin.loader.JarPluginLoader;
import org.zohar.framework.core.extension.plugin.loader.PluginLoader;
import org.zohar.framework.core.extension.util.file.FileUtils;

/**
 * Default implementation of the {@link PluginManager} interface.
 * In essence it is a {@link ZipPluginManager} plus a {@link JarPluginManager}.
 * So, it can load plugins from jar and zip, simultaneous.
 *
 * <p>This class is not thread-safe.
 *
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public class DefaultPluginManager extends AbstractPluginManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultPluginManager.class);

    public static final String PLUGINS_DIR_CONFIG_PROPERTY_NAME = "pf4j.pluginsConfigDir";

    public DefaultPluginManager() {
        super();
    }

    public DefaultPluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    public DefaultPluginManager(List<Path> pluginsRoots) {
        super(pluginsRoots);
    }

    @Override
    protected PluginDescriptorFinder createPluginDescriptorFinder() {
        return new CompoundPluginDescriptorFinder()
                .add(new PropertiesPluginDescriptorFinder())
                .add(new ManifestPluginDescriptorFinder());
    }

    @Override
    protected ExtensionFinder createExtensionFinder() {
        DefaultExtensionFinder extensionFinder = new DefaultExtensionFinder(this);
        addPluginStateListener(extensionFinder);

        return extensionFinder;
    }

    @Override
    protected PluginFactory createPluginFactory() {
        return new DefaultPluginFactory();
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new DefaultExtensionFactory();
    }

    @Override
    protected PluginStatusProvider createPluginStatusProvider() {
        String configDir = System.getProperty(PLUGINS_DIR_CONFIG_PROPERTY_NAME);
        Path configPath = configDir != null
                ? Paths.get(configDir)
                : getPluginsRoots().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No pluginsRoot configured"));

        return new DefaultPluginStatusProvider(configPath);
    }

    @Override
    protected PluginRepository createPluginRepository() {
        return new CompoundPluginRepository()
                .add(new DevelopmentPluginRepository(getPluginsRoots()), this::isDevelopment)
                .add(new JarPluginRepository(getPluginsRoots()), this::isNotDevelopment)
                .add(new DefaultPluginRepository(getPluginsRoots()), this::isNotDevelopment);
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new CompoundPluginLoader()
                .add(new DevelopmentPluginLoader(this), this::isDevelopment)
                .add(new JarPluginLoader(this), this::isNotDevelopment)
                .add(new DefaultPluginLoader(this), this::isNotDevelopment);
    }

    @Override
    protected VersionManager createVersionManager() {
        return new DefaultVersionManager();
    }

    @Override
    protected void initialize() {
        super.initialize();

        if (isDevelopment()) {
            addPluginStateListener(new LoggingPluginStateListener());
        }

        log.info("PF4J version {} in '{}' mode", getVersion(), getRuntimeMode());
    }

    /**
     * Load a plugin from disk. If the path is a zip file, first unpack.
     *
     * @param pluginPath plugin location on disk
     * @return PluginWrapper for the loaded plugin or null if not loaded
     * @throws PluginRuntimeException if problems during load
     */
    @Override
    protected PluginWrapper loadPluginFromPath(Path pluginPath) {
        // First unzip any ZIP files
        try {
            pluginPath = FileUtils.expandIfZip(pluginPath);
        } catch (Exception e) {
            log.warn("Failed to unzip " + pluginPath, e);
            return null;
        }

        return super.loadPluginFromPath(pluginPath);
    }

}