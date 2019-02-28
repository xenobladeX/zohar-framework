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
package com.xenoblade.zohar.framework.commons.api.enums.dynamic;

import sun.misc.ClassLoaderUtil;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DynamicEnum
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class DynamicEnum<E extends DynamicEnum<E>> {
    private static Map<Class<? extends DynamicEnum<?>>, Map<String, DynamicEnum<?>>> elements =
            new LinkedHashMap<Class<? extends DynamicEnum<?>>, Map<String, DynamicEnum<?>>>();

    private final String name;

    public final String name() {
        return name;
    }

    private final int ordinal;

    public final int ordinal() {
        return ordinal;
    }

    protected DynamicEnum(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
        Map<String, DynamicEnum<?>> typeElements = elements.get(getClass());
        if (typeElements == null) {
            typeElements = new LinkedHashMap<String, DynamicEnum<?>>();
            elements.put(getDynaEnumClass(), typeElements);
        }
        typeElements.put(name, this);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends DynamicEnum<?>> getDynaEnumClass() {
        return (Class<? extends DynamicEnum<?>>)getClass();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public final boolean equals(Object other) {
        return this == other;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public final int compareTo(E other) {
        DynamicEnum<?> self = this;
        if (self.getClass() != other.getClass() && // optimization
                self.getDeclaringClass() != other.getDeclaringClass())
            throw new ClassCastException();
        return self.ordinal - other.ordinal();
    }

    @SuppressWarnings("unchecked")
    public final Class<E> getDeclaringClass() {
        Class clazz = getClass();
        Class zuper = clazz.getSuperclass();
        return (zuper == DynamicEnum.class) ? clazz : zuper;
    }

    @SuppressWarnings("unchecked")
    public static <T extends DynamicEnum<T>> T valueOf(Class<T> enumType, String name) {
        return (T)elements.get(enumType).get(name);
    }

    @SuppressWarnings("unused")
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        throw new InvalidObjectException("can't deserialize enum");
    }

    @SuppressWarnings("unused")
    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize enum");
    }

    @Override
    protected final void finalize() { }


    public static <E> DynamicEnum<? extends DynamicEnum<?>>[] values() {
        throw new IllegalStateException("Sub class of DynamicEnum must implement staic method valus()");
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] values(Class<E> enumType) {
        Class clazz = enumType;
        List<E> typedValues = new ArrayList<E>();

        while (clazz != DynamicEnum.class) {
            Collection<DynamicEnum<?>> values =  elements.get(enumType).values();
            values.stream().forEach(value -> typedValues.add((E)value));
            clazz = clazz.getSuperclass();
        }

        return typedValues.toArray((E[])Array.newInstance(enumType, typedValues.size()));
    }
}
