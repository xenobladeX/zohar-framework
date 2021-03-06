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
package com.xenoblade.zohar.framework.core.common.pf4j.extension.factory;

import com.xenoblade.zohar.framework.commons.api.enums.IEnum;

/**
 * ZoharEnumExtensionCreator
 * @author xenoblade
 * @since 1.0.0
 */
public class ZoharEnumExtensionCreator extends AbstractExtensionCreator {

    @Override
    public Boolean match(Class<?> extensionClass) {
        return extensionClass.isEnum() && IEnum.class.isAssignableFrom(extensionClass);
    }

    @Override
    public <T> T create(Class<T> extensionClass) {
        return null;
    }

    @Override public Integer order() {
        return 0;
    }
}
