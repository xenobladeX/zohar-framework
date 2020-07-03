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

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultExtensionFactory;

import java.util.List;

/**
 * ZoharExtensionFactory
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class ZoharExtensionFactory extends DefaultExtensionFactory {

    private List<ExtensionCreator> extensionCreators = Lists.newArrayList();

    public ZoharExtensionFactory() {
        addCreator(new ZoharEnumExtensionCreator());
    }

    @Override
    public <T> T create(Class<T> extensionClass) {
        for (ExtensionCreator extensionCreator : extensionCreators) {
            if(extensionCreator.match(extensionClass)) {
                return extensionCreator.create(extensionClass);
            }
        }
        return defaultCreate(extensionClass);
    }

    public void addCreator(ExtensionCreator extensionCreator) {
        extensionCreators.add(extensionCreator);
    }

    protected <T> T defaultCreate(Class<T> extensionClass) {
        return super.create(extensionClass);
    }

}
