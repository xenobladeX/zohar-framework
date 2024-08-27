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
package org.zohar.framework.core.extension.exception;

import org.zohar.framework.core.extension.util.StringUtils;

/**
 * An exception used to indicate that a plugin problem occurred.
 * It's a generic plugin exception class to be thrown when no more specific class is applicable.
 *
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public class PluginRuntimeException extends RuntimeException {

    public PluginRuntimeException() {
        super();
    }

    public PluginRuntimeException(String message) {
        super(message);
    }

    public PluginRuntimeException(Throwable cause) {
        super(cause);
    }

    public PluginRuntimeException(Throwable cause, String message, Object... args) {
        super(StringUtils.format(message, args), cause);
    }

    public PluginRuntimeException(String message, Object... args) {
        super(StringUtils.format(message, args));
    }

}