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

import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.commons.api.enums.BasicZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.enums.IEnum;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import com.xenoblade.zohar.framework.core.common.pf4j.enums.EnumFactory;
import com.xenoblade.zohar.framework.core.common.pf4j.enums.ZoharEnumFactory;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.ExtensionWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * ZoharExtensionFinderFilter
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class ZoharEnumExtensionFinderFilter implements ExtensionFinderFilter {

    private EnumFactory enumFactory = ZoharEnumFactory.INSTANCE;


    /**
     * 是否匹配
     * @param extensionWrapper
     * @return
     */
    @Override public Boolean match(ExtensionWrapper extensionWrapper) {
        Class extensionClass = extensionWrapper.getDescriptor().extensionClass;
        return extensionClass.isEnum() && IEnum.class.isAssignableFrom(extensionClass);
    }

    /**
     * 处理
     * @param extensionWrapper
     * @return
     */
    @Override public ExtensionWrapper filter(ExtensionWrapper extensionWrapper, String pluginId) {
        Class extensionClass = extensionWrapper.getDescriptor().extensionClass;
        // get all enums
        try {
            // invoke method values()
            Method method = extensionClass.getMethod("values");
            Object result = method.invoke(null);
            if (result != null) {
                // add to enumFactory
                List<IEnum> zoharEnums = Arrays.asList((IEnum[])result);
                zoharEnums.stream().forEach(zoharEnum -> {
                    enumFactory.add(zoharEnum, pluginId);
                });
            } else {
                log.warn("No enum found in {}, will ignore", extensionClass.getName());
            }

        } catch (NoSuchMethodException e) {
            throw new ZoharException(
                    StrUtil.format("class {} no such method {}", extensionClass.getName(),
                            "values()"), BasicZoharErrorCode.ENUM_PARSE_ERROR);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ZoharException(StrUtil.format("class {} invoke method {} failed: ",
                    extensionClass.getName(), "values()", e.getMessage()),
                    BasicZoharErrorCode.ENUM_PARSE_ERROR);
        }
        return extensionWrapper;
    }
}
