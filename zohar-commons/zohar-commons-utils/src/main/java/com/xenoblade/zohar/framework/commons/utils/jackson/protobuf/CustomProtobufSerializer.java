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
package com.xenoblade.zohar.framework.commons.utils.jackson.protobuf;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CustomProtobufSerializer
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class CustomProtobufSerializer<T extends MessageOrBuilder> extends ProtobufSerializer<T> {

    private static final String NULL_VALUE_FULL_NAME = NullValue.getDescriptor().getFullName();
    private static Long NUMBER_MAX_VALUE = Math.round(Math.pow(2,52));
    private final Map<Class<?>, JsonSerializer<Object>> serializerCache = new ConcurrentHashMap();

    public CustomProtobufSerializer(Class<T> protobufType) {
        super(protobufType);
    }

    protected void writeMap(FieldDescriptor field, Object entries, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        Descriptor entryDescriptor = field.getMessageType();
        FieldDescriptor keyDescriptor = entryDescriptor.findFieldByName("key");
        FieldDescriptor valueDescriptor = entryDescriptor.findFieldByName("value");
        generator.writeStartObject();
        Iterator var8 = ((List)entries).iterator();

        while(var8.hasNext()) {
            Message entry = (Message)var8.next();
            generator.writeFieldName(entry.getField(keyDescriptor).toString());
            Object value = entry.getField(valueDescriptor);
            this.writeValue(valueDescriptor, value, generator, serializerProvider);
        }

        generator.writeEndObject();
    }

    protected void writeValue(FieldDescriptor field, Object value, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        switch(field.getJavaType()) {
            case INT:
                generator.writeNumber((Integer)value);
                break;
            case LONG:
                // 解决 json number 类型有最大值限制导致部分Long 字段显示不全的问题
                generator.writeString(String.valueOf((Long)value));
                break;
            case FLOAT:
                generator.writeNumber((Float)value);
                break;
            case DOUBLE:
                generator.writeNumber((Double)value);
                break;
            case BOOLEAN:
                generator.writeBoolean((Boolean)value);
                break;
            case STRING:
                generator.writeString((String)value);
                break;
            case ENUM:
                EnumValueDescriptor enumDescriptor = (EnumValueDescriptor)value;
                if (NULL_VALUE_FULL_NAME.equals(enumDescriptor.getType().getFullName())) {
                    generator.writeNull();
                } else if (writeEnumsUsingIndex(serializerProvider)) {
                    generator.writeNumber(enumDescriptor.getNumber());
                } else {
                    generator.writeString(enumDescriptor.getName());
                }
                break;
            case BYTE_STRING:
                generator.writeString(serializerProvider.getConfig().getBase64Variant().encode(((ByteString)value).toByteArray()));
                break;
            case MESSAGE:
                Class<?> subType = value.getClass();
                JsonSerializer<Object> serializer = (JsonSerializer)this.serializerCache.get(subType);
                if (serializer == null) {
                    serializer = serializerProvider.findValueSerializer(value.getClass(), (BeanProperty)null);
                    this.serializerCache.put(subType, serializer);
                }

                serializer.serialize(value, generator, serializerProvider);
                break;
            default:
                throw unrecognizedType(field, generator);
        }

    }

    private static boolean writeEnumsUsingIndex(SerializerProvider config) {
        return config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    }

    private static IOException unrecognizedType(FieldDescriptor field, JsonGenerator generator) throws IOException {
        String error = StrUtil.format("Unrecognized java type '{}' for field {}", field.getJavaType(), field.getFullName());
        throw new JsonGenerationException(error, generator);
    }

}
