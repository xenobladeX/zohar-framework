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

import java.io.IOException;

/**
 * SpecialSerializeFactory
 * @author xenoblade
 * @since 1.0.0
 */
public interface SpecialSerializeFactory {

    /**
     * Is support deserialize.
     *
     * @param type the type
     * @return true, if successful
     */
    boolean supportDeserialize(int type);

    /**
     * Parses the object
     *
     * @param input the input
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    Object parse(Hessian2WithSpecialObjectInput input) throws IOException;

    /**
     * Try serialize object.
     *
     * @param output the output
     * @param obj the obj
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    boolean trySerializeObject(Hessian2WithSpecialObjectOutput output, Object obj)
            throws IOException;
}

