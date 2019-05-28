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
package com.xenoblade.zohar.framework.commons.log.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * ExceptionSerizlizer
 * @author xenoblade
 * @since 1.0.0
 */
public class ExceptionSerizlizer extends JsonSerializer<Throwable> {

    @Override public void serialize(Throwable e, JsonGenerator jsonGenerator,
                                    SerializerProvider serializerProvider) throws IOException {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        jsonGenerator.writeString(errors.toString());
    }

    @Override public void serializeWithType(Throwable value, JsonGenerator gen,
                                            SerializerProvider serializers, TypeSerializer typeSer)
            throws IOException {
        super.serializeWithType(value, gen, serializers, typeSer);
    }
}
