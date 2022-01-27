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

import com.xenoblade.zohar.framework.core.extension.enumeration.EnumFactory;
import com.xenoblade.zohar.framework.core.extension.enumeration.IEnum;
import com.xenoblade.zohar.framework.core.extension.enumeration.ZoharEnumFactory;
import com.xenoblade.zohar.framework.core.extension.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * ZoharEnumExtensionFinderFilter
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class ZoharEnumExtensionFinderFilter implements ExtensionFinderFilter {

    private static final Logger log = LoggerFactory.getLogger(ZoharEnumExtensionFinderFilter.class);

    private EnumFactory enumFactory = ZoharEnumFactory.INSTANCE;


    /**
     * 是否匹配
     *
     * @param extensionWrapper
     * @return
     */
    @Override
    public Boolean match(ExtensionWrapper extensionWrapper) {
        Class extensionClass = extensionWrapper.getDescriptor().extensionClass;
        return extensionClass.isEnum() && IEnum.class.isAssignableFrom(extensionClass);
    }

    /**
     * process {@link ExtensionWrapper}
     *
     * @param extensionWrapper
     * @return
     */
    @Override
    public ExtensionWrapper filter(ExtensionWrapper extensionWrapper) {
        Class extensionClass = extensionWrapper.getDescriptor().extensionClass;
        // get all enums
        try {
            // invoke method values()
            Method method = extensionClass.getMethod("values");
            Object result = method.invoke(null);
            if (result != null) {
                // add to enumFactory
                List<IEnum> zoharEnums = Arrays.asList((IEnum[]) result);
                zoharEnums.stream().forEach(zoharEnum -> {
                    enumFactory.add(zoharEnum);
                });
            } else {
                log.warn("No enum found in {}, will ignore", extensionClass.getName());
            }

        } catch (NoSuchMethodException e) {
            throw new ExtensionRuntimeException(e,
                    StringUtils.format("class {} no such method {}",
                            extensionClass.getName(), "values()"));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ExtensionRuntimeException(e, StringUtils.format("class {} invoke method {} failed: ",
                    extensionClass.getName(), "values()", e.getMessage()));
        }
        return extensionWrapper;
    }
}