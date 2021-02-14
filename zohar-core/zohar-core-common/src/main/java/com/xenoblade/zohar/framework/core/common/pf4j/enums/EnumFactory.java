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

import com.xenoblade.zohar.framework.commons.api.enums.IEnum;
import com.xenoblade.zohar.framework.commons.utils.type.TypeReference;

/**
 * ZoharEnumFactory
 * @author xenoblade
 * @since 1.0.0
 */
public interface EnumFactory {

    /**
     * 添加{@link IEnum}
     * @param e
     * @return
     */
    <T extends Comparable> Boolean add(IEnum<T> e, String pluginId);

    /**
     * 删除对应插件的枚举扩展
     * @param pluginId
     * @return
     */
    void removeFromPlugin(String pluginId);

    /**
     * 通过 value 和类型得到对应的{@link IEnum}
     * @param value
     * @param <T>
     * @return
     */
    <T extends Comparable, E extends IEnum<T>> E valueOf(T value, Class<E> enumClass);

    /**
     * 通过 value 和类型得到对应的{@link ZoharEnumWrapper}
     * @param value
     * @param enumClass
     * @param <T>
     * @param <E>
     * @return
     */
     <T extends Comparable, E extends IEnum<T>> ZoharEnumWrapper<E> wrapperOf(T value, Class<E> enumClass);

}
