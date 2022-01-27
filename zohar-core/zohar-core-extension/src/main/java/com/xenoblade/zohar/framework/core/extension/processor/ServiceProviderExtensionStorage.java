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
package com.xenoblade.zohar.framework.core.extension.processor;

import com.xenoblade.zohar.framework.core.extension.Extension;

import javax.annotation.processing.FilerException;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores {@link Extension}s in {@code META-INF/services}.
 *
 * @author Decebal Suiu
 * @since 0.0.1
 */
public class ServiceProviderExtensionStorage extends ExtensionStorage {

    public static final String EXTENSIONS_RESOURCE = "META-INF/services";

    public ServiceProviderExtensionStorage(ExtensionAnnotationProcessor processor) {
        super(processor);
    }

    @Override
    public Map<String, Set<String>> read() {
        Map<String, Set<String>> extensions = new HashMap<>();

        for (String extensionPoint : processor.getExtensions().keySet()) {
            try {
                FileObject file = getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", EXTENSIONS_RESOURCE
                        + "/" + extensionPoint);
                Set<String> entries = new HashSet<>();
                ExtensionStorage.read(file.openReader(true), entries);
                extensions.put(extensionPoint, entries);
            } catch (FileNotFoundException | NoSuchFileException e) {
                // doesn't exist, ignore
            } catch (FilerException e) {
                // re-opening the file for reading or after writing is ignorable
            } catch (IOException e) {
                error(e.getMessage());
            }
        }

        return extensions;
    }

    @Override
    public void write(Map<String, Set<String>> extensions) {
        for (Map.Entry<String, Set<String>> entry : extensions.entrySet()) {
            String extensionPoint = entry.getKey();
            try {
                FileObject file = getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", EXTENSIONS_RESOURCE
                        + "/" + extensionPoint);
                try (BufferedWriter writer = new BufferedWriter(file.openWriter())) {
                    // write header
                    writer.write("# Generated by PF4J"); // write header
                    writer.newLine();
                    // write extensions
                    for (String extension : entry.getValue()) {
                        writer.write(extension);
                        if (!isExtensionOld(extensionPoint, extension)) {
                            writer.write(" # pf4j extension");
                        }
                        writer.newLine();
                    }
                }
            } catch (FileNotFoundException e) {
                // it's the first time, create the file
            } catch (FilerException e) {
                // re-opening the file for reading or after writing is ignorable
            } catch (IOException e) {
                error(e.toString());
            }
        }
    }

    private boolean isExtensionOld(String extensionPoint, String extension) {
        return processor.getOldExtensions().containsKey(extensionPoint)
                && processor.getOldExtensions().get(extensionPoint).contains(extension);
    }

}