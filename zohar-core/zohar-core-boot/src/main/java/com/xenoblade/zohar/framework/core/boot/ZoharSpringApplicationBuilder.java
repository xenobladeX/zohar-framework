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

import com.xenoblade.zohar.framework.core.extension.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.ResourceLoader;

/**
 * ZoharSpringApplicationBuilder
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class ZoharSpringApplicationBuilder extends SpringApplicationBuilder {

    private ExtensionFinder extensionFinder;

    public ZoharSpringApplicationBuilder(Class<?>... sources) {
        super(sources);
    }

    public ZoharSpringApplicationBuilder(ResourceLoader resourceLoader, Class<?>... sources) {
        super(resourceLoader, sources);
    }

    @Override
    protected SpringApplication createSpringApplication(ResourceLoader resourceLoader, Class<?>... sources) {
        // custom extensionFinder
        this.extensionFinder = customExtensionFinder();

        ZoharSpringApplication zoharSpringApplication = new ZoharSpringApplication(resourceLoader, sources);
        zoharSpringApplication.setExtensionFinder(this.extensionFinder);
        return zoharSpringApplication;
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

    @Override
    public ZoharSpringApplication build(String... args) {
        ZoharSpringApplication zoharSpringApplication = (ZoharSpringApplication)super.build(args);
        zoharSpringApplication.setExtensionFinder(this.extensionFinder);
        return zoharSpringApplication;
    }

    public void setExtensionFinder(ExtensionFinder extensionFinder) {
        this.extensionFinder = extensionFinder;
    }

    public ExtensionFinder getExtensionFinder() {
        return extensionFinder;
    }

}