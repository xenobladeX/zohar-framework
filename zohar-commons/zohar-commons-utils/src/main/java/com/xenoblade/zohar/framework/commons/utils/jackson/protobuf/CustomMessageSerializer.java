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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Descriptors.FileDescriptor.Syntax;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.GeneratedMessageV3.ExtendableMessageOrBuilder;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ExtensionRegistryWrapper;
import com.hubspot.jackson.datatype.protobuf.PropertyNamingStrategyWrapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CustomMessageSerializer
 * @author xenoblade
 * @since 1.0.0
 */
public class CustomMessageSerializer extends CustomProtobufSerializer<MessageOrBuilder> {

    @SuppressFBWarnings({"SE_BAD_FIELD"})
    private final ProtobufJacksonConfig config;

    /** @deprecated */
    @Deprecated
    public CustomMessageSerializer(ExtensionRegistryWrapper extensionRegistry) {
        this(ProtobufJacksonConfig.builder().extensionRegistry(extensionRegistry).build());
    }

    public CustomMessageSerializer(ProtobufJacksonConfig config) {
        super(MessageOrBuilder.class);
        this.config = config;
    }

    public void serialize(MessageOrBuilder message, JsonGenerator generator, SerializerProvider serializerProvider) throws
            IOException {
        generator.writeStartObject();
        boolean proto3 = message.getDescriptorForType().getFile().getSyntax() == Syntax.PROTO3;
        Include include = serializerProvider.getConfig().getDefaultPropertyInclusion().getValueInclusion();
        boolean writeDefaultValues = proto3 && include != Include.NON_DEFAULT;
        boolean writeEmptyCollections = include != Include.NON_DEFAULT && include != Include.NON_EMPTY;
        PropertyNamingStrategyBase namingStrategy = new PropertyNamingStrategyWrapper(serializerProvider.getConfig().getPropertyNamingStrategy());
        Descriptor descriptor = message.getDescriptorForType();
        List<FieldDescriptor> fields = new ArrayList(descriptor.getFields());
        Iterator var11;
        if (message instanceof ExtendableMessageOrBuilder) {
            var11 = this.config.extensionRegistry().getExtensionsByDescriptor(descriptor).iterator();

            while(var11.hasNext()) {
                ExtensionInfo extensionInfo = (ExtensionInfo)var11.next();
                fields.add(extensionInfo.descriptor);
            }
        }

        var11 = fields.iterator();

        while(true) {
            while(true) {
                List valueList;
                FieldDescriptor field;
                label67:
                do {
                    while(var11.hasNext()) {
                        field = (FieldDescriptor)var11.next();
                        if (field.isRepeated()) {
                            valueList = (List)message.getField(field);
                            continue label67;
                        }

                        if (!message.hasField(field) && (!writeDefaultValues || supportsFieldPresence(field) || field.getContainingOneof() != null)) {
                            if (include == Include.ALWAYS && field.getContainingOneof() == null) {
                                generator.writeFieldName(namingStrategy.translate(field.getName()));
                                generator.writeNull();
                            }
                        } else {
                            generator.writeFieldName(namingStrategy.translate(field.getName()));
                            this.writeValue(field, message.getField(field), generator, serializerProvider);
                        }
                    }

                    generator.writeEndObject();
                    return;
                } while(valueList.isEmpty() && !writeEmptyCollections);

                if (field.isMapField()) {
                    generator.writeFieldName(namingStrategy.translate(field.getName()));
                    this.writeMap(field, valueList, generator, serializerProvider);
                } else if (valueList.size() == 1 && writeSingleElementArraysUnwrapped(serializerProvider)) {
                    generator.writeFieldName(namingStrategy.translate(field.getName()));
                    this.writeValue(field, valueList.get(0), generator, serializerProvider);
                } else {
                    generator.writeArrayFieldStart(namingStrategy.translate(field.getName()));
                    Iterator var14 = valueList.iterator();

                    while(var14.hasNext()) {
                        Object subValue = var14.next();
                        this.writeValue(field, subValue, generator, serializerProvider);
                    }

                    generator.writeEndArray();
                }
            }
        }
    }

    private static boolean supportsFieldPresence(FieldDescriptor field) {
        return field.getJavaType() == JavaType.MESSAGE;
    }

    private static boolean writeEmptyArrays(SerializerProvider config) {
        return config.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    }

    private static boolean writeSingleElementArraysUnwrapped(SerializerProvider config) {
        return config.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
    }

}
