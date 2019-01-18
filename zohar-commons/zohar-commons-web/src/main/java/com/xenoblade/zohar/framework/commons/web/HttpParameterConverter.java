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
package com.xenoblade.zohar.framework.commons.web;

import com.xenoblade.zohar.framework.commons.utils.DateTimeUtils;
import org.apache.commons.beanutils.BeanMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * HttpParameterConverter
 * @author xenoblade
 * @since 1.0.0
 */
public class HttpParameterConverter {

    private Map<String, Object> beanMap;

    private Map<String, String> parameter = new HashMap<>();

    private String prefix = "";

    private static final Map<Class, Function<Object, String>> convertMap = new HashMap<>();

    private static Function<Object, String> defaultConvert = String::valueOf;

    private static final Set<Class> basicClass = new HashSet<>();

    static {
        basicClass.add(int.class);
        basicClass.add(double.class);
        basicClass.add(float.class);
        basicClass.add(byte.class);
        basicClass.add(short.class);
        basicClass.add(char.class);
        basicClass.add(boolean.class);

        basicClass.add(Integer.class);
        basicClass.add(Double.class);
        basicClass.add(Float.class);
        basicClass.add(Byte.class);
        basicClass.add(Short.class);
        basicClass.add(Character.class);
        basicClass.add(String.class);
        basicClass.add(Boolean.class);

        basicClass.add(Date.class);


        putConvert(Date.class, (date) -> DateTimeUtils.format(date, DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND));


    }

    @SuppressWarnings("unchecked")
    private static <T> void putConvert(Class<T> type, Function<T, String> convert) {
        convertMap.put(type, (Function) convert);

    }

    private String convertValue(Object value) {
        return convertMap.getOrDefault(value.getClass(), defaultConvert).apply(value);
    }

    @SuppressWarnings("unchecked")
    public HttpParameterConverter(Object bean) {
        if (bean instanceof Map) {
            beanMap = ((Map) bean);
        } else {
            beanMap = new HashMap<>((Map) new BeanMap(bean));
            beanMap.remove("class");
            beanMap.remove("declaringClass");
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private void doConvert(String key, Object value) {
        if (value == null) {
            return;
        }
        if(value instanceof Class){
            return;
        }
        Class type = org.springframework.util.ClassUtils.getUserClass(value);

        if (basicClass.contains(type) || value instanceof Number || value instanceof Enum) {
            parameter.put(getParameterKey(key), convertValue(value));
            return;
        }

        if (value instanceof Object[]) {
            value = Arrays.asList(((Object[]) value));
        }

        if (value instanceof Collection) {
            Collection coll = ((Collection) value);
            int count = 0;
            for (Object o : coll) {
                doConvert(key + "[" + count++ + "]", o);
            }
        } else {
            HttpParameterConverter converter = new HttpParameterConverter(value);
            converter.setPrefix(getParameterKey(key).concat("."));
            parameter.putAll(converter.convert());
        }
    }

    private void doConvert() {
        beanMap.forEach(this::doConvert);
    }


    private String getParameterKey(String property) {
        return prefix.concat(property);
    }

    public Map<String, String> convert() {
        doConvert();

        return parameter;
    }

}
