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
package com.xenoblade.zohar.framework.sample.pf4j.app;

import com.xenoblade.zohar.framework.commons.api.enums.IZoharErrorCode;
import com.xenoblade.zohar.framework.core.common.pf4j.ZoharExtensionsManager;
import com.xenoblade.zohar.framework.core.common.pf4j.enums.ZoharEnumFactory;
import com.xenoblade.zohar.framework.sample.pf4j.app.service.Greetings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Application
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class Application {

    @SneakyThrows
    public static void main(String[] args) {
        // retrieves the spring application context
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Pf4jConfiguration.class);

        // retrieves automatically the extensions for the Greeting.class extension point\
        Greetings greetings = applicationContext.getBean(Greetings.class);
        greetings.printGreetings();

        // IEnum
        IZoharErrorCode plugin1ErrorCode = ZoharEnumFactory.INSTANCE.valueOf(2000, IZoharErrorCode.class);
        IZoharErrorCode plugin2ErrorCode = ZoharEnumFactory.INSTANCE.valueOf(3000, IZoharErrorCode.class);
        log.info("plugin1ErrorCode: {}, plugin2ErrorCode: {}", plugin1ErrorCode, plugin2ErrorCode);

        // stop plugins
        ZoharExtensionsManager zoharExtensionsManager = applicationContext.getBean(ZoharExtensionsManager.class);
        PluginManager pluginManager = zoharExtensionsManager.getPluginManager();
        /*
        // retrieves manually the extensions for the Greeting.class extension point
        List<Greeting> greetings = pluginManager.getExtensions(Greeting.class);
        System.out.println("greetings.size() = " + greetings.size());
        */
        pluginManager.stopPlugins();

        greetings = applicationContext.getBean(Greetings.class);
        greetings.printGreetings();
    }

}
