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
package org.zohar.framework.core.plugin.util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClassUtils
 *
 * @author Decebal Suiu
 * @since 0.0.1
 */
public class ClassUtils {

    private ClassUtils() {}

    public static List<String> getAllInterfacesNames(Class<?> aClass) {
        return toString(getAllInterfaces(aClass));
    }

    public static List<Class<?>> getAllInterfaces(Class<?> aClass) {
        List<Class<?>> list = new ArrayList<>();

        while (aClass != null) {
            Class<?>[] interfaces = aClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if (!list.contains(anInterface)) {
                    list.add(anInterface);
                }

                List<Class<?>> superInterfaces = getAllInterfaces(anInterface);
                for (Class<?> superInterface : superInterfaces) {
                    if (!list.contains(superInterface)) {
                        list.add(superInterface);
                    }
                }
            }

            aClass = aClass.getSuperclass();
        }

        return list;
    }


    /**
     * Uses {@link Class#getSimpleName()} to convert from {@link Class} to {@link String}.
     */
    private static List<String> toString(List<Class<?>> classes) {
        List<String> list = new ArrayList<>();

        for (Class<?> aClass : classes) {
            list.add(aClass.getSimpleName());
        }

        return list;
    }

}