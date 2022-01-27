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

import com.xenoblade.zohar.framework.core.extension.util.StringUtils;

/**
 * An exception used to indicate that a extension problem occurred.
 * It's a generic extension exception class to be thrown when no more specific class is applicable.
 *
 * @author Decebal Suiu
 * @since 0.0.1
 */
public class ExtensionRuntimeException extends RuntimeException {

    public ExtensionRuntimeException() {
        super();
    }

    public ExtensionRuntimeException(String message) {
        super(message);
    }

    public ExtensionRuntimeException(Throwable cause) {
        super(cause);
    }

    public ExtensionRuntimeException(Throwable cause, String message, Object... args) {
        super(StringUtils.format(message, args), cause);
    }

    public ExtensionRuntimeException(String message, Object... args) {
        super(StringUtils.format(message, args));
    }

}