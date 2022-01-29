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
package com.xenoblade.zohar.framework.core.boot;

import cn.hutool.core.util.ClassUtil;
import com.xenoblade.zohar.framework.core.boot.extension.SpringExtensionCreator;
import com.xenoblade.zohar.framework.core.boot.launcher.ApplicationLauncher;
import com.xenoblade.zohar.framework.core.extension.ExtensionFinder;
import com.xenoblade.zohar.framework.core.extension.ZoharExtensionFactory;
import com.xenoblade.zohar.framework.core.extension.ZoharExtensionFinder;
import com.xenoblade.zohar.framework.core.extension.enumeration.IEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;

/**
 * The spring application starter for Zohar Framework
 *
 * @author xenoblade
 * @since 0.0.1
 */
@Slf4j
public class ZoharSpringApplication extends SpringApplication{

    private ExtensionFinder extensionFinder;

    public ZoharSpringApplication(Class<?>... primarySources) {
        super(primarySources);
    }

    public ZoharSpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        super(resourceLoader, primarySources);
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run(new Class[]{primarySource}, args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        SpringApplicationBuilder builder = createSpringApplicationBuilder(primarySources, args);
        return (builder.build(args)).run(args);
    }

    public static SpringApplicationBuilder createSpringApplicationBuilder(Class<?>[] primarySources, String... args) {
        ZoharSpringApplicationBuilder builder = new ZoharSpringApplicationBuilder(primarySources);

        // extension load zohar enums
        builder.getExtensionFinder().find(IEnum.class);
        // extension load launcher
        builder.getExtensionFinder().find(ApplicationLauncher.class).forEach(extensionWrapper -> {
            extensionWrapper.getExtension().launcher(builder);
        });

        return builder;
    }

    @Override
    @SneakyThrows
    public ConfigurableApplicationContext createApplicationContext(){
        ConfigurableApplicationContext context = super.createApplicationContext();

        // add SpringExtensionCreator
        ZoharExtensionFinder zoharExtensionFinder = (ZoharExtensionFinder)this.extensionFinder;
        ZoharExtensionFactory zoharExtensionFactory = (ZoharExtensionFactory)zoharExtensionFinder.getExtensionFactory();
        zoharExtensionFactory.addCreator(new SpringExtensionCreator(context));
        // register extensionFinder to spring context
        context.getBeanFactory().registerSingleton(ClassUtil.getClassName(this.extensionFinder.getClass(), true), this.extensionFinder);

        return context;
    }

    public void setExtensionFinder(ExtensionFinder extensionFinder) {
        this.extensionFinder = extensionFinder;
    }

    public ExtensionFinder getExtensionFinder() {
        return extensionFinder;
    }
}