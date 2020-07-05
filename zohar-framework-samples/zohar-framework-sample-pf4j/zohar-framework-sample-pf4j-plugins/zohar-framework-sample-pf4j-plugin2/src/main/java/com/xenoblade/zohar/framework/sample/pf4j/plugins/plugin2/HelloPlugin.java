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
package com.xenoblade.zohar.framework.sample.pf4j.plugins.plugin2;

import com.xenoblade.zohar.framework.core.spring.pf4j.SpringPlugin;
import com.xenoblade.zohar.framework.sample.pf4j.api.Greeting;
import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * HelloPlugin
 * @author xenoblade
 * @since 1.0.0
 */
public class HelloPlugin extends SpringPlugin {

    public HelloPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("HelloPlugin.start()");
    }

    @Override
    public void stop() {
        System.out.println("HelloPlugin.stop()");
        super.stop(); // to close applicationContext
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(SpringConfiguration.class);
        applicationContext.refresh();

        return applicationContext;
    }

    @Extension(ordinal=1)
    public static class HelloGreeting implements Greeting {

        @Autowired
        private MessageProvider messageProvider;

        @Override
        public String getGreeting() {
            //            return "Hello";
            // complicate a little bit the code
            return messageProvider.getMessage();
        }

    }

}
