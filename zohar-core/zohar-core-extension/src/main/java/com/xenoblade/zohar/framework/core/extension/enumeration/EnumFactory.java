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

/**
 * EnumFactory
 *
 * @author xenoblade
 * @since 0.0.1
 */
public interface EnumFactory {

    /**
     * 添加{@link IEnum}
     * @param e
     * @return
     */
    <T extends Comparable> Boolean add(IEnum<T> e);


    /**
     * 通过 value 和类型得到对应的{@link IEnum}
     * @param value
     * @param <T>
     * @return
     */
    public <T extends Comparable, E extends IEnum<T>> E valueOf(T value, Class<E> enumClass);

}