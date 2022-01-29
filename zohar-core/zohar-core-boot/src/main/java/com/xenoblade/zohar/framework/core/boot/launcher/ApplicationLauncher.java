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
package com.xenoblade.zohar.framework.core.boot.launcher;

import com.xenoblade.zohar.framework.core.extension.ExtensionPoint;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * ApplicationLauncher
 *
 * @author xenoblade
 * @since 0.0.1
 */
public interface ApplicationLauncher extends ExtensionPoint {

    void launcher(SpringApplicationBuilder builder);

}