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
package com.xenoblade.zohar.framework.dubbo.serialize.protobufmix;

import com.alibaba.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Hessian2WithSpecialObjectOutput
 * @author xenoblade
 * @since 1.0.0
 */
public class Hessian2WithSpecialObjectOutput extends Hessian2ObjectOutput {

    private List<SpecialSerializeFactory> factoryList;

    /**
     * Instantiates a new hessian2 with special object output.
     *
     * @param os the os
     */
    public Hessian2WithSpecialObjectOutput(OutputStream os) {
        super(os);
    }

    /**
     * Instantiates a new hessian2 with special object output.
     *
     * @param os the os
     * @param factories the factories
     */
    public Hessian2WithSpecialObjectOutput(OutputStream os, SpecialSerializeFactory... factories) {
        super(os);

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
    public void writeObject(Object obj) throws IOException {
        if (null != factoryList) {
            for (SpecialSerializeFactory factory : factoryList) {
                if (factory.trySerializeObject(this, obj)) {
                    return;
                }
            }
        }
        // Using hession2 serializing for other object
        super.writeInt(ProtobufUtil.TYPE_NORMAL);
        super.writeObject(obj);
    }

}
