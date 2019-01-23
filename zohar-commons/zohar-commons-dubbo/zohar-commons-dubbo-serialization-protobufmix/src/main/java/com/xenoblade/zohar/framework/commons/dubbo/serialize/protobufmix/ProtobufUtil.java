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
package com.xenoblade.zohar.framework.commons.dubbo.serialize.protobufmix;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * protocol buffer utils
 * @author xenoblade
 * @since 1.0.0
 */
public class ProtobufUtil {

    // normal java object
    public static final int TYPE_NORMAL = 0;

    // protobuf object
    public static final int TYPE_PROTOBUF = 1;

    private static final String BUILDER_CLASS_SUFFIX = "$Builder";

    private static final Map<String, Builder> BUILDER_CACHE = new ConcurrentHashMap<String, Builder>();

    /**
     * Parse data according to className
     *
     * @param className the class name
     * @param data the data
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Object parseFrom(String className, byte[] data) throws IOException {
        try {
            boolean isBuilderClass = className.endsWith(BUILDER_CLASS_SUFFIX);
            if (isBuilderClass) {
                className = className.substring(0,
                        className.length() - BUILDER_CLASS_SUFFIX.length());
            }

            MessageLite.Builder builder = getPbBuilder(className);
            builder.mergeFrom(data);

            if (isBuilderClass) {
                return builder;
            } else {
                return builder.buildPartial();
            }
        } catch (Exception ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IOException(ex);
            }
        }
    }

    /**
     * Gets the pb builder.
     *
     * @param className the class name
     * @return the pb builder
     * @throws ClassNotFoundException the class not found exception
     * @throws SecurityException the security exception
     * @throws NoSuchMethodException the no such method exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static MessageLite.Builder getPbBuilder(String className)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        MessageLite.Builder builder = BUILDER_CACHE.get(className);
        if (null == builder) {
            Class messageClass = Class.forName(className);

            Method newBuilder = messageClass.getMethod("newBuilder");
            builder = (MessageLite.Builder) newBuilder.invoke(null);

            BUILDER_CACHE.put(className, builder);
        }

        return builder.clone();
    }
}

