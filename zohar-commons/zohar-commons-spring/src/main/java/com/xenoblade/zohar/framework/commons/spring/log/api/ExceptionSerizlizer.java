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
package com.xenoblade.zohar.framework.commons.spring.log.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;

/**
 * ExceptionSerizlizer
 * @author xenoblade
 * @since 1.0.0
 */
public class ExceptionSerizlizer extends JsonSerializer<Exception> {

    @Override public void serialize(Exception e, JsonGenerator jsonGenerator,
                                    SerializerProvider serializerProvider) throws IOException{
        jsonGenerator.writeString(e.toString());
    }

    @Override public void serializeWithType(Exception value, JsonGenerator gen,
                                            SerializerProvider serializers, TypeSerializer typeSer)
            throws IOException {
        super.serializeWithType(value, gen, serializers, typeSer);
    }
}
