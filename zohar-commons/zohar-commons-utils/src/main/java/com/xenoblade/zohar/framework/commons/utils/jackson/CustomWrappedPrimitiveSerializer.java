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
package com.xenoblade.zohar.framework.commons.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;

import java.io.IOException;

/**
 * CustomWrappedPrimitiveSerializer
 * @author xenoblade
 * @since 1.0.0
 */
public class CustomWrappedPrimitiveSerializer<T extends MessageOrBuilder> extends CustomProtobufSerializer<T> {

    public CustomWrappedPrimitiveSerializer(Class<T> wrapperType) {
        super(wrapperType);
    }

    public void serialize(MessageOrBuilder message, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        FieldDescriptor field = message.getDescriptorForType().findFieldByName("value");
        Object value = message.getField(field);
        this.writeValue(field, value, generator, serializerProvider);
    }

}
