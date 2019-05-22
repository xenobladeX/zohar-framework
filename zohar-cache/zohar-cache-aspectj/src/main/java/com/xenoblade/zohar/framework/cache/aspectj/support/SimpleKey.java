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
package com.xenoblade.zohar.framework.cache.aspectj.support;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * A simple key as returned from the {@link SimpleKeyGenerator}.
 *
 * @author Phillip Webb
 * @see SimpleKeyGenerator
 * @since 4.0
 */
public class SimpleKey implements Serializable {

    private static final long serialVersionUID = 7860535724041259057L;

    private final String methodStr;

    private final Object[] params;

    private final int hashCode;


    /**
     * Create a new {@link SimpleKey} instance.
     *
     * @param elements the elements of the key
     */
    public SimpleKey(Method method, Object... elements) {
        Assert.notNull(elements, "Elements must not be null");
        this.params = new Object[elements.length];
        System.arraycopy(elements, 0, this.params, 0, elements.length);
        int hashCode = Arrays.deepHashCode(this.params);
        this.methodStr = method.toString();
        this.hashCode = hashCode * 59 + (this.methodStr == null ? 43 : this.methodStr.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj || (obj instanceof SimpleKey
                && this.methodStr.equals(((SimpleKey) obj).methodStr)) &&
                Arrays.deepEquals(this.params, ((SimpleKey) obj).params));
    }

    @Override
    public final int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder(getClass().getSimpleName());
        toString.append("(method=")
                .append(this.methodStr)
                .append(", params=").append("[")
                .append(StringUtils.arrayToCommaDelimitedString(this.params))
                .append("])");
        return toString.toString();
    }

    public Object[] getParams() {
        return params;
    }

}

