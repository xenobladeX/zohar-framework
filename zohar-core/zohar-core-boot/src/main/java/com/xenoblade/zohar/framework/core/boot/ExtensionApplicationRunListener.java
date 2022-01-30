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

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.core.boot.extension.ExtensionComponent;
import com.xenoblade.zohar.framework.core.boot.extension.SpringExtensionCreator;
import com.xenoblade.zohar.framework.core.boot.launcher.ApplicationLauncher;
import com.xenoblade.zohar.framework.core.extension.*;
import com.xenoblade.zohar.framework.core.extension.enumeration.IEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * ExtensionApplicationRunListener
 *
 * @author xenoblade
 * @since 0.0.1
 */
@Slf4j
public class ExtensionApplicationRunListener implements SpringApplicationRunListener {

    private ExtensionFinder extensionFinder;

    public ExtensionApplicationRunListener(SpringApplication application, String[]  args){
        this.extensionFinder = customExtensionFinder();
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {

    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        // extension load zohar enums
        this.extensionFinder.find(IEnum.class);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        // add SpringExtensionCreator
        if (!(this.extensionFinder instanceof ZoharExtensionFinder)) {
            throw new RuntimeException("extensionFinder must be ZoharExtensionFinder");
        }
        ZoharExtensionFinder zoharExtensionFinder = (ZoharExtensionFinder)this.extensionFinder;
        ZoharExtensionFactory zoharExtensionFactory = (ZoharExtensionFactory)zoharExtensionFinder.getExtensionFactory();
        zoharExtensionFactory.addCreator(new SpringExtensionCreator(context));
        // register extensionFinder to spring context
        String extensionFinderBeanName = StrUtil.lowerFirst(ClassUtil.getClassName(this.extensionFinder.getClass(), true));
        context.getBeanFactory().registerSingleton(extensionFinderBeanName, this.extensionFinder);
        // extension load launcher
        this.extensionFinder.find(ApplicationLauncher.class).forEach(extensionWrapper -> {
            extensionWrapper.getExtension().launcher(context);
        });

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        log.info("application contextLoaded");
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        log.info("application started");
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }

    protected ExtensionFinder customExtensionFinder() {
        ExtensionFactory zoharExtensionFactory = new ZoharExtensionFactory();
        ZoharExtensionFinder zoharExtensionFinder = new ZoharExtensionFinder(zoharExtensionFactory);
        // add enum extension finder filter
        zoharExtensionFinder.addFilter(new ZoharEnumExtensionFinderFilter());
        MultipleExtensionFinder multipleExtensionFinder = new MultipleExtensionFinder();
        multipleExtensionFinder.add(zoharExtensionFinder);
        return zoharExtensionFinder;
    }
    
}