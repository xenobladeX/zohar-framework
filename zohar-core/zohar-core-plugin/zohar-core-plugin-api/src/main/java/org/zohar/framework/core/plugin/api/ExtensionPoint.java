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
package org.zohar.framework.core.plugin.api;

/**
 * An plugin point is a formal declaration in a plugin (or in application API) where customization is allowed.
 * It's a place where custom code can be "plugged in".
 * <p>
 * An plugin point is defined by an interface or an abstract class.
 * The plugin point is used by the application to discover and use the custom implementations.
 *
 * @author Decebal Suiu
 * @since 0.0.1
 */
public interface ExtensionPoint {
}