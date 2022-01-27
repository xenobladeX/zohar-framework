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
import com.xenoblade.zohar.framework.core.extension.processor.ServiceProviderExtensionStorage;
import com.xenoblade.zohar.framework.core.extension.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * The {@link java.util.ServiceLoader} base implementation for {@link ExtensionFinder}.
 * This class lookup extensions in all extensions index files {@code META-INF/services}.
 *
 * @author Decebal Suiu
 * @since 0.0.1
 */
public class ServiceProviderExtensionFinder extends AbstractExtensionFinder {

    private static final Logger log = LoggerFactory.getLogger(ServiceProviderExtensionFinder.class);

    public static final String EXTENSIONS_RESOURCE = ServiceProviderExtensionStorage.EXTENSIONS_RESOURCE;

    public ServiceProviderExtensionFinder(ExtensionFactory extensionFactory) {
        super(extensionFactory);
    }

    @Override
    public Set<String> readClasspathStorages() {
        log.debug("Reading extensions storages from classpath");
        Set<String> result = new HashSet<>();

        final Set<String> bucket = new HashSet<>();
        try {
            Enumeration<URL> urls = getClass().getClassLoader().getResources(EXTENSIONS_RESOURCE);
            if (urls.hasMoreElements()) {
                collectExtensions(urls, bucket);
            } else {
                log.debug("Cannot find '{}'", EXTENSIONS_RESOURCE);
            }

            debugExtensions(bucket);

            result.addAll(bucket);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    private void collectExtensions(Enumeration<URL> urls, Set<String> bucket) throws URISyntaxException, IOException {
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            log.debug("Read '{}'", url.getFile());
            collectExtensions(url, bucket);
        }
    }

    private void collectExtensions(URL url, Set<String> bucket) throws URISyntaxException, IOException {
        Path extensionPath;
        if (url.toURI().getScheme().equals("jar")) {
            extensionPath = FileUtils.getPath(url.toURI(), EXTENSIONS_RESOURCE);
        } else {
            extensionPath = Paths.get(url.toURI());
        }

        bucket.addAll(readExtensions(extensionPath));
    }

    private Set<String> readExtensions(Path extensionPath) throws IOException {
        final Set<String> result = new HashSet<>();
        Files.walkFileTree(extensionPath, Collections.<FileVisitOption>emptySet(), 1, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.debug("Read '{}'", file);
                try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    ExtensionStorage.read(reader, result);
                }

                return FileVisitResult.CONTINUE;
            }

        });

        return result;
    }

}