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

import com.xenoblade.zohar.framework.core.extension.processor.ExtensionStorage;
import com.xenoblade.zohar.framework.core.extension.processor.LegacyExtensionStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * All extensions declared in a plugin are indexed in a file {@code META-INF/extensions.idx}.
 * This class lookup extensions in all extensions index files {@code META-INF/extensions.idx}.
 *
 * @author Decebal Suiu
 * @since 0.0.1
 */
public class LegacyExtensionFinder extends AbstractExtensionFinder {

    private static final Logger log = LoggerFactory.getLogger(LegacyExtensionFinder.class);

    public static final String EXTENSIONS_RESOURCE = LegacyExtensionStorage.EXTENSIONS_RESOURCE;

    public LegacyExtensionFinder(ExtensionFactory extensionFactory) {
        super(extensionFactory);
    }

    @Override
    public Set<String> readClasspathStorages() {
        log.debug("Reading extensions storages from classpath");
        Set<String> result = new HashSet<>();

        Set<String> bucket = new HashSet<>();
        try {
            Enumeration<URL> urls = getClass().getClassLoader().getResources(EXTENSIONS_RESOURCE);
            if (urls.hasMoreElements()) {
                collectExtensions(urls, bucket);
            } else {
                log.debug("Cannot find '{}'", EXTENSIONS_RESOURCE);
            }

            debugExtensions(bucket);

            result.addAll(bucket);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }



    private void collectExtensions(Enumeration<URL> urls, Set<String> bucket) throws IOException {
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            log.debug("Read '{}'", url.getFile());
            collectExtensions(url.openStream(), bucket);
        }
    }

    private void collectExtensions(InputStream inputStream, Set<String> bucket) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            ExtensionStorage.read(reader, bucket);
        }
    }

}