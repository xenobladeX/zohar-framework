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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;

/**
 * PropertiesDynamicEnum
 * @author xenoblade
 * @since 1.0.0
 */
public class PropertiesDynamicEnum extends DynamicEnum<PropertiesDynamicEnum> {

    protected PropertiesDynamicEnum(String name, int ordinal) {
        super(name, ordinal);
    }

    public static <E> DynamicEnum<? extends DynamicEnum<?>>[] values() {
        return values(PropertiesDynamicEnum.class);
    }

    protected static <E> void init(Class<E> clazz) {
        try {
            initProps(clazz);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static <E> void initProps(Class<E> clazz) throws Exception {
        String rcName = clazz.getName().replace('.', '/') + ".properties";
        BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(rcName)));

        Constructor<E> minimalConstructor = getConstructor(clazz, new Class[] {String.class, int.class});
        Constructor<E> additionalConstructor = getConstructor(clazz, new Class[] {String.class, int.class, String.class});
        int ordinal = 0;
        for (String line = reader.readLine();  line != null;  line = reader.readLine()) {
            line = line.replaceFirst("#.*", "").trim();
            if (line.equals("")) {
                continue;
            }
            String[] parts = line.split("\\s*=\\s*");
            if (parts.length == 1 || additionalConstructor == null) {
                minimalConstructor.newInstance(parts[0], ordinal);
            } else {
                additionalConstructor.newInstance(parts[0], ordinal, parts[1]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <E> Constructor<E> getConstructor(Class<E> clazz, Class<?>[] argTypes) {
        for(Class<?> c = clazz;  c != null;  c = c.getSuperclass()) {
            try {
                return (Constructor<E>)c.getDeclaredConstructor(String.class, int.class, String.class);
            } catch(Exception e) {
                continue;
            }
        }
        return null;
    }

}
