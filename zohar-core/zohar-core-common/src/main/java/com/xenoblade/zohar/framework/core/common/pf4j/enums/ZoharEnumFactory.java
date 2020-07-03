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
package com.xenoblade.zohar.framework.core.common.pf4j.enums;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xenoblade.zohar.framework.commons.api.enums.IEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ZoharEnumFactory
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class ZoharEnumFactory implements EnumFactory {

    private Map<Class<IEnum<? extends Comparable>>, List<IEnum<? extends Comparable>>> enumMap = Maps
            .newConcurrentMap();

    public static final ZoharEnumFactory INSTANCE = new ZoharEnumFactory();

    /**
     * 添加{@link IEnum}
     * @param e
     * @return
     */
    @Override public <T extends Comparable> Boolean add(IEnum<T> e) {
        Class enumClass = findEnumClass(e.getClass());
        Boolean ret = false;
        if (enumClass != null) {
            synchronized (enumMap) {
                List<IEnum<? extends Comparable>> enumList = enumMap.get(enumClass);
                if (enumList == null) {
                    enumMap.put(enumClass, Lists.newArrayList(e));
                    ret = true;
                } else {
                    Boolean isExist = enumList.stream().anyMatch(x -> x.value().equals(e.value()));
                    if (!isExist) {
                        enumList.add(e);
                        ret = true;
                    } else {
                        log.debug("zohar enum {}, name {}, value {} already exist, will ignore", enumClass.getName(), e.name(), e.value());
                    }
                }
            }
        } else {
            log.debug("Class {} is not extends from {}", e.getClass().getName(), IEnum.class.getName());
        }
        return ret;
    }

    /**
     * 通过 value 得到对应的{@link IEnum}
     * @param value
     * @return
     */
    @Override public <T extends Comparable, E extends IEnum<T>> E valueOf(T value, Class<E> enumClass) {
        List<? extends IEnum> enumList = enumMap.get(enumClass);
        if (enumList != null) {
            IEnum<T> targetEnum = enumList.stream().collect(Collectors.toMap(IEnum::value, Function.identity())).get(value);
            if (targetEnum == null) {
                log.warn("Can't find value {} in enum {}", value, enumClass.getName());
            }
            return (E)targetEnum;
        } else {
            log.warn("Can't find value {} in enum {}", value, enumClass.getName());
        }
        return null;
    }

    private Class<? extends IEnum> findEnumClass(Class<? extends IEnum> targetClass) {
        Class<?>[] interfaces = targetClass.getInterfaces();
        Class enumClass = null;
        List<Class<?>> interfaceList = Lists.newArrayList(interfaces);
        for (Class<?> aInterface : interfaceList) {
            if (IEnum.class.isAssignableFrom(aInterface)) {
                enumClass = aInterface;
                break;
            }
        }
        return enumClass;
    }

}
