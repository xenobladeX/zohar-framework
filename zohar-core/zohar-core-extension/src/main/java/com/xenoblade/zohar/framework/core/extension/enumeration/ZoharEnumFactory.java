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
package com.xenoblade.zohar.framework.core.extension.enumeration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ZoharEnumFactory
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class ZoharEnumFactory implements EnumFactory {

    private static final Logger log = LoggerFactory.getLogger(ZoharEnumFactory.class);

    private Map<Class<IEnum<? extends Comparable>>, List<IEnum>> enumMap = Maps
            .newConcurrentMap();

    public static final ZoharEnumFactory INSTANCE = new ZoharEnumFactory();

    /**
     * 添加{@link IEnum}
     *
     * @param e
     * @return
     */
    @Override
    public <T extends Comparable> Boolean add(IEnum<T> e) {
        Class enumClass = findEnumClass(e.getClass());
        Boolean ret = false;
        if (enumClass != null) {
            synchronized (enumMap) {
                List<IEnum> enumList = enumMap.get(enumClass);
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
     *
     * @param value
     * @return
     */
    @Override
    public <T extends Comparable, E extends IEnum<T>> E valueOf(T value, Class<E> enumClass) {
        List<IEnum> enumList = enumMap.get(enumClass);
        if (enumList != null) {
            E targetEnum = (E)enumList.stream().collect(Collectors.toMap(e -> e.value(), Function.identity())).get(value);
            if (targetEnum == null) {
                log.warn("Can't find value {} in enum {}", value, enumClass.getName());
            }
            return targetEnum;
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