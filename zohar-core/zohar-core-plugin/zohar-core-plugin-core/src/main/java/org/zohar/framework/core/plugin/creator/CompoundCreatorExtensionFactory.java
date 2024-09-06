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
package org.zohar.framework.core.plugin.creator;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;

/**
 * ZoharExtensionFactory
 *
 * @author xenobladde
 * @since 0.0.1
 */
public class CompoundCreatorExtensionFactory extends DefaultExtensionFactory {

    private List<ExtensionCreator> extensionCreators = Lists.newArrayList();

    public CompoundCreatorExtensionFactory() {
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
        extensionCreators.sort(Comparator.comparingInt(ExtensionCreator::order));
    }

    protected <T> T defaultCreate(Class<T> extensionClass) {
        return super.create(extensionClass);
    }

}