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
package com.xenoblade.zohar.framework.sample.pf4j.spring.plugin.b;

import com.xenoblade.zohar.framework.core.spring.pf4j.SpringPlugin;
import com.xenoblade.zohar.framework.sample.pf4j.spring.plugin.b.config.BConfiguration;
import org.pf4j.PluginWrapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * ASpringPlugin
 * @author xenoblade
 * @since 1.0.0
 */
public class BSpringPlugin extends SpringPlugin {

    public BSpringPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override protected GenericApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(BConfiguration.class);

        return applicationContext;
    }
}
