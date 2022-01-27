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

import com.xenoblade.zohar.framework.core.extension.asm.ExtensionInfo;
import com.xenoblade.zohar.framework.core.extension.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * AbstractExtensionFinder
 *
 * @author Decebal Suiu
 * @since 0.0.1
 */
public abstract class AbstractExtensionFinder implements ExtensionFinder{

    private static final Logger log = LoggerFactory.getLogger(AbstractExtensionFinder.class);

    protected ExtensionFactory extensionFactory;
    protected volatile Set<String> entries; // cache by pluginId
    protected volatile Map<String, ExtensionInfo> extensionInfos; // cache extension infos by class name
    protected Boolean checkForExtensionDependencies = null;

    public AbstractExtensionFinder(ExtensionFactory extensionFactory) {
        this.extensionFactory = extensionFactory;
    }

    public abstract Set<String> readClasspathStorages();

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<ExtensionWrapper<T>> find(Class<T> type) {
        log.debug("Finding extensions of extension point '{}'", type.getName());
        List<ExtensionWrapper<T>> result = new ArrayList<>();

        // classpath's extensions <=> pluginId = null
        Set<String> classNames = findClassNames();
        if (classNames == null || classNames.isEmpty()) {
            return result;
        }

        log.trace("Checking extensions from classpath");

        ClassLoader classLoader = getClass().getClassLoader();

        for (String className : classNames) {
            try {
                if (isCheckForExtensionDependencies()) {
                    // Load extension annotation without initializing the class itself.
                    //
                    // If optional dependencies are used, the class loader might not be able
                    // to load the extension class because of missing optional dependencies.
                    //
                    // Therefore we're extracting the extension annotation via asm, in order
                    // to extract the required plugins for an extension. Only if all required
                    // plugins are currently available and started, the corresponding
                    // extension is loaded through the class loader.
                    ExtensionInfo extensionInfo = getExtensionInfo(className, classLoader);
                    if (extensionInfo == null) {
                        log.error("No extension annotation was found for '{}'", className);
                        continue;
                    }
                }

                log.debug("Loading class '{}' using class loader '{}'", className, classLoader);
                Class<?> extensionClass = classLoader.loadClass(className);

                log.debug("Checking extension type '{}'", className);
                if (type.isAssignableFrom(extensionClass)) {
                    ExtensionWrapper extensionWrapper = createExtensionWrapper(extensionClass);
                    result.add(extensionWrapper);
                    log.debug("Added extension '{}' with ordinal {}", className, extensionWrapper.getOrdinal());
                } else {
                    log.trace("'{}' is not an extension for extension point '{}'", className, type.getName());
                }
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (result.isEmpty()) {
            log.debug("No extensions found for extension point '{}'", type.getName());
        } else {
            log.debug("Found {} extensions for extension point '{}'", result.size(), type.getName());
        }

        // sort by "ordinal" property
        Collections.sort(result);

        return result;
    }

    @Override
    public List<ExtensionWrapper> find() {
        log.debug("Finding extensions");
        List<ExtensionWrapper> result = new ArrayList<>();

        Set<String> classNames = findClassNames();
        if (classNames.isEmpty()) {
            return result;
        }

        log.trace("Checking extensions from classpath");

        ClassLoader classLoader = getClass().getClassLoader();

        for (String className : classNames) {
            try {
                log.debug("Loading class '{}' using class loader '{}'", className, classLoader);
                Class<?> extensionClass = classLoader.loadClass(className);

                ExtensionWrapper extensionWrapper = createExtensionWrapper(extensionClass);
                result.add(extensionWrapper);
                log.debug("Added extension '{}' with ordinal {}", className, extensionWrapper.getOrdinal());
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                log.error(e.getMessage(), e);
            }
        }

        if (result.isEmpty()) {
            log.debug("No extensions found");
        } else {
            log.debug("Found {} extensions", result.size());
        }

        // sort by "ordinal" property
        Collections.sort(result);

        return result;
    }

    @Override
    public Set<String> findClassNames() {
        return getEntries();
    }

    public final boolean isCheckForExtensionDependencies() {
        return Boolean.TRUE.equals(checkForExtensionDependencies);
    }

    public void setCheckForExtensionDependencies(boolean checkForExtensionDependencies) {
        this.checkForExtensionDependencies = checkForExtensionDependencies;
    }


    protected void debugExtensions(Set<String> extensions) {
        if (log.isDebugEnabled()) {
            if (extensions.isEmpty()) {
                log.debug("No extensions found");
            } else {
                log.debug("Found possible {} extensions:", extensions.size());
                for (String extension : extensions) {
                    log.debug("   " + extension);
                }
            }
        }
    }

    private Set<String> readStorages() {
        Set<String> result = new HashSet<>();

        result.addAll(readClasspathStorages());

        return result;
    }

    private Set<String> getEntries() {
        if (entries == null) {
            entries = readStorages();
        }

        return entries;
    }

    /**
     * Returns the parameters of an {@link Extension} annotation without loading
     * the corresponding class into the class loader.
     *
     * @param className name of the class, that holds the requested {@link Extension} annotation
     * @param classLoader class loader to access the class
     * @return the contents of the {@link Extension} annotation or null, if the class does not
     * have an {@link Extension} annotation
     */
    private ExtensionInfo getExtensionInfo(String className, ClassLoader classLoader) {
        if (extensionInfos == null) {
            extensionInfos = new HashMap<>();
        }

        if (!extensionInfos.containsKey(className)) {
            log.trace("Load annotation for '{}' using asm", className);
            ExtensionInfo info = ExtensionInfo.load(className, classLoader);
            if (info == null) {
                log.warn("No extension annotation was found for '{}'", className);
                extensionInfos.put(className, null);
            } else {
                extensionInfos.put(className, info);
            }
        }

        return extensionInfos.get(className);
    }

    private ExtensionWrapper createExtensionWrapper(Class<?> extensionClass) {
        Extension extensionAnnotation = findExtensionAnnotation(extensionClass);
        int ordinal = extensionAnnotation != null ? extensionAnnotation.ordinal() : 0;
        ExtensionDescriptor descriptor = new ExtensionDescriptor(ordinal, extensionClass);

        return new ExtensionWrapper<>(descriptor, extensionFactory);
    }

    public static Extension findExtensionAnnotation(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Extension.class)) {
            return clazz.getAnnotation(Extension.class);
        }

        // search recursively through all annotations
        for (Annotation annotation : clazz.getAnnotations()) {
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            if (!annotationClass.getName().startsWith("java.lang.annotation")) {
                Extension extensionAnnotation = findExtensionAnnotation(annotationClass);
                if (extensionAnnotation != null) {
                    return extensionAnnotation;
                }
            }
        }

        return null;
    }

    private void checkDifferentClassLoaders(Class<?> type, Class<?> extensionClass) {
        ClassLoader typeClassLoader = type.getClassLoader(); // class loader of extension point
        ClassLoader extensionClassLoader = extensionClass.getClassLoader();
        boolean match = ClassUtils.getAllInterfacesNames(extensionClass).contains(type.getSimpleName());
        if (match && !extensionClassLoader.equals(typeClassLoader)) {
            // in this scenario the method 'isAssignableFrom' returns only FALSE
            // see http://www.coderanch.com/t/557846/java/java/FWIW-FYI-isAssignableFrom-isInstance-differing
            log.error("Different class loaders: '{}' (E) and '{}' (EP)", extensionClassLoader, typeClassLoader);
        }
    }


}