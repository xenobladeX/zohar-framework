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
package com.xenoblade.zohar.framework.core.spring.pf4j.config;

import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.core.spring.pf4j.SpringPluginClassLoader;
import com.xenoblade.zohar.framework.core.spring.pf4j.SpringPluginManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.CompoundPluginLoader;
import org.pf4j.DevelopmentPluginLoader;
import org.pf4j.JarPluginLoader;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginLoader;
import org.pf4j.PluginManager;
import org.pf4j.PluginStateListener;
import org.pf4j.PluginStatusProvider;
import org.pf4j.RuntimeMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Path;

/**
 * Pf4jSpringTestConfiguration
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ComponentScan("com.xenoblade.zohar.framework.core.spring")
public class Pf4jSpringTestConfiguration {

    @Bean
    public PluginStateListener pluginStateListener() {
        return event -> {
            PluginDescriptor descriptor = event.getPlugin().getDescriptor();
            if (log.isDebugEnabled()) {
                log.debug("Plugin [{}（{}）]({}) {}", descriptor.getPluginId(),
                        descriptor.getVersion(), descriptor.getPluginDescription(),
                        event.getPluginState().toString());
            }
        };
    }

    @Bean("pluginManager")
    @SneakyThrows
    public SpringPluginManager pluginManager(Pf4jProperties properties) {
        // Setup RuntimeMode
        System.setProperty("pf4j.mode", properties.getRuntimeMode().toString());

        // Setup Plugin folder
        String pluginsRoot = StringUtils.hasText(properties.getPluginsRoot()) ? properties.getPluginsRoot() : "plugins";
        System.setProperty("pf4j.pluginsDir", pluginsRoot);
        String appHome = System.getProperty("app.home");
        if (RuntimeMode.DEPLOYMENT == RuntimeMode.byName(properties.getRuntimeMode())
                && StringUtils.hasText(appHome)) {
            System.setProperty("pf4j.pluginsDir", appHome + File.separator + pluginsRoot);
        }
        SpringPluginManager pluginManager = new SpringPluginManager(
                new File(pluginsRoot).toPath()) {
            @Override
            protected PluginLoader createPluginLoader() {
                if (StrUtil.isNotEmpty(properties.getCustomPluginLoader())) {
                    try {
                        Class clazz = ClassLoader.getSystemClassLoader().loadClass(properties.getCustomPluginLoader());
                        Constructor<?> constructor = clazz.getConstructor(PluginManager.class);
                        return (PluginLoader) constructor.newInstance(this);
                    } catch (Exception ex) {
                        throw new IllegalArgumentException(String.format("Create custom PluginLoader %s failed. Make sure" +
                                "there is a constructor with one argument that accepts PluginLoader", properties.getCustomPluginLoader()));
                    }
                } else {
                    return new CompoundPluginLoader()
                            .add(new DevelopmentPluginLoader(this) {
                                @Override
                                protected PluginClassLoader createPluginClassLoader(Path pluginPath,
                                                                                    PluginDescriptor pluginDescriptor) {
                                    if (properties.getClassesDirectories() != null && properties.getClassesDirectories().length > 0) {
                                        for (String classesDirectory : properties.getClassesDirectories()) {
                                            pluginClasspath.addClassesDirectories(classesDirectory);
                                        }
                                    }
                                    if (properties.getLibDirectories() != null && properties.getLibDirectories().length > 0) {
                                        for (String libDirectory : properties.getLibDirectories()) {
                                            pluginClasspath.addJarsDirectories(libDirectory);
                                        }
                                    }
                                    return new SpringPluginClassLoader(pluginManager,
                                            pluginDescriptor, getClass().getClassLoader());
                                }
                            }, this::isDevelopment)
                            .add(new JarPluginLoader(this) {
                                @Override
                                public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor) {
                                    PluginClassLoader pluginClassLoader = new SpringPluginClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader());
                                    pluginClassLoader.addFile(pluginPath.toFile());
                                    return pluginClassLoader;
                                }
                            }, this::isNotDevelopment);
                }
            }

            @Override
            protected PluginStatusProvider createPluginStatusProvider() {
                if (PropertyPluginStatusProvider.isPropertySet(properties)) {
                    return new PropertyPluginStatusProvider(properties);
                }
                return super.createPluginStatusProvider();
            }
        };

        pluginManager.setProfiles(properties.getPluginProfiles());
        pluginManager.setExactVersionAllowed(Boolean.valueOf(properties.getExactVersionAllowed()));
        pluginManager.setSystemVersion(properties.getSystemVersion());

        return pluginManager;
    }

}
