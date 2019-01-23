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

import com.alibaba.dubbo.common.serialize.hessian2.Hessian2ObjectInput;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Object input
 * @author xenoblade
 * @since 1.0.0
 */
public class Hessian2WithSpecialObjectInput extends Hessian2ObjectInput {
    private List<SpecialSerializeFactory> factoryList;

    /**
     * Instantiates a new hessian2 with special object input.
     *
     * @param is the is
     */
    public Hessian2WithSpecialObjectInput(InputStream is) {
        super(is);
    }

    /**
     * Instantiates a new hessian2 with special object input.
     *
     * @param is the is
     * @param factories the factories
     */
    public Hessian2WithSpecialObjectInput(InputStream is, SpecialSerializeFactory... factories) {
        super(is);

        if (null != factories) {
            factoryList = new ArrayList<SpecialSerializeFactory>();
            for (SpecialSerializeFactory factory : factories) {
                factoryList.add(factory);
            }
        }
    }

    /**
     * Adds the serialize factory.
     *
     * @param factory the factory
     */
    public void addSerializeFactory(SpecialSerializeFactory factory) {
        if (null == factoryList) {
            factoryList = new ArrayList<SpecialSerializeFactory>();
        }

        factoryList.add(factory);
    }

    @Override
    public Object readObject() throws IOException {
        // get the object tag
        int type = super.readInt();
        if (null != factoryList) {
            for (SpecialSerializeFactory factory : factoryList) {
                if (factory.supportDeserialize(type)) {
                    return factory.parse(this);
                }
            }
        }

        return super.readObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException {
        return (T) readObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        return (T) readObject();
    }
}

