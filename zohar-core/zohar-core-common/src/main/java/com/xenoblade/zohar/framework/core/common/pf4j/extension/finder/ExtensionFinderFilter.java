/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.core.common.pf4j.extension.finder;

import org.pf4j.ExtensionWrapper;

/**
 * ExtensionFinderFilter
 * @author xenoblade
 * @since 1.0.0
 */
public interface ExtensionFinderFilter {

    /**
     * 是否匹配
     * @param extensionWrapper
     * @return
     */
    Boolean match(ExtensionWrapper extensionWrapper);

    /**
     * 处理
     * @param extensionWrapper
     * @return
     */
    ExtensionWrapper filter(ExtensionWrapper extensionWrapper, String pluginId);
}
