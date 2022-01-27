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

/**
 * Extension
 *
 * @author xenoblade
 * @since 0.0.1
 */
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Decebal Suiu
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
@Documented
public @interface Extension {

    int ordinal() default 0;

    /**
     * An array of extension points, that are implemented by this extension.
     * This explicit configuration overrides the automatic detection of extension points in the
     * {@link com.xenoblade.zohar.framework.core.extension.processor.ExtensionAnnotationProcessor}.
     * <p>
     * In case your extension is directly derived from an extension point this attribute is NOT required.
     * But under certain <a href="https://github.com/pf4j/pf4j/issues/264">more complex scenarios</a> it
     * might be useful to explicitly set the extension points for an extension.
     *
     * @return classes of extension points, that are implemented by this extension
     */
    Class<? extends ExtensionPoint>[] points() default {};

}