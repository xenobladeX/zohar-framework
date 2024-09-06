/*
 * Copyright [2024] [xenoblade]
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
package org.zohar.framework.core.plugin.api.util;

import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * @author xenoblade
 * @Date 2024/9/5
 * @since 0.0.1-SNAPSHOT
 */
public class ClassUtils {

    private ClassUtils() {}

    /**
     * Get a certain annotation of a {@link TypeElement}.
     * See <a href="https://stackoverflow.com/a/10167558">stackoverflow.com</a> for more information.
     *
     * @param typeElement the type element, that contains the requested annotation
     * @param annotationClass the class of the requested annotation
     * @return the requested annotation or null, if no annotation of the provided class was found
     * @throws NullPointerException if <code>typeElement</code> or <code>annotationClass</code> is null
     */
    public static AnnotationMirror getAnnotationMirror(TypeElement typeElement, Class<?> annotationClass) {
        String annotationClassName = annotationClass.getName();
        for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {
            if (m.getAnnotationType().toString().equals(annotationClassName)) {
                return m;
            }
        }

        return null;
    }

    /**
     * Get a certain parameter of an {@link AnnotationMirror}.
     * See <a href="https://stackoverflow.com/a/10167558">stackoverflow.com</a> for more information.
     *
     * @param annotationMirror the annotation, that contains the requested parameter
     * @param annotationParameter the name of the requested annotation parameter
     * @return the requested parameter or null, if no parameter of the provided name was found
     * @throws NullPointerException if <code>annotationMirror</code> is null
     */
    public static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String annotationParameter) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(annotationParameter)) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * Get a certain annotation parameter of a {@link TypeElement}.
     * See <a href="https://stackoverflow.com/a/10167558">stackoverflow.com</a> for more information.
     *
     * @param typeElement the type element, that contains the requested annotation
     * @param annotationClass the class of the requested annotation
     * @param annotationParameter the name of the requested annotation parameter
     * @return the requested parameter or null, if no annotation for the provided class was found or no annotation parameter was found
     * @throws NullPointerException if <code>typeElement</code> or <code>annotationClass</code> is null
     */
    public static AnnotationValue getAnnotationValue(TypeElement typeElement, Class<?> annotationClass, String annotationParameter) {
        AnnotationMirror annotationMirror = getAnnotationMirror(typeElement, annotationClass);
        return annotationMirror != null ? getAnnotationValue(annotationMirror, annotationParameter) : null;
    }

}