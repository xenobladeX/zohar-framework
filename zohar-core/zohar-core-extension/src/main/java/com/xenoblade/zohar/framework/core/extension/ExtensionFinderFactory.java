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
package com.xenoblade.zohar.framework.core.extension;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * ExtensionFinderFactory
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class ExtensionFinderFactory {

    private static ExtensionFinder EXTENSION_FINDER;

    public static ExtensionFinder getExtensionFinder() {
        if (EXTENSION_FINDER == null) {
            EXTENSION_FINDER = initExtensionFinder();
        }
        return EXTENSION_FINDER;
    }

    private static ExtensionFinder initExtensionFinder() {
        ExtensionFactory zoharExtensionFactory = new ZoharExtensionFactory();
        ZoharExtensionFinder zoharExtensionFinder = new ZoharExtensionFinder(zoharExtensionFactory);
        // add enum extension finder filter
        zoharExtensionFinder.addFilter(new ZoharEnumExtensionFinderFilter());
        MultipleExtensionFinder multipleExtensionFinder = new MultipleExtensionFinder();
        multipleExtensionFinder.add(zoharExtensionFinder);

        // custom extension finder by spi
        ServiceLoader<ExtensionFinderConfigurer> extensionFinderConfigurers = ServiceLoader.load(ExtensionFinderConfigurer.class);
        List<ExtensionFinderConfigurer> extensionFinderConfigurerList = new ArrayList<>();
        extensionFinderConfigurers.forEach(extensionFinderConfigurer -> extensionFinderConfigurerList.add(extensionFinderConfigurer));
        extensionFinderConfigurerList.stream().sorted(Comparator.comparingInt(ExtensionFinderConfigurer::order)).forEach(extensionFinderConfigurer -> {
             extensionFinderConfigurer.customExtensionFinder(zoharExtensionFinder);
        });

        return zoharExtensionFinder;
    }


}