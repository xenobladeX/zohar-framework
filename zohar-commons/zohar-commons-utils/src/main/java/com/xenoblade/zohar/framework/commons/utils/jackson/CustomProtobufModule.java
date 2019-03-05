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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.google.protobuf.BoolValue;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Duration;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.FieldMask;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.ListValue;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.NullValue;
import com.google.protobuf.StringValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.MessageDeserializerFactory;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.DurationDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.FieldMaskDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.ListValueDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.NullValueDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.StructDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.TimestampDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.ValueDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.WrappedPrimitiveDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.DurationSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.FieldMaskSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.ListValueSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.NullValueSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.StructSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.TimestampSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.ValueSerializer;

/**
 * CustomProtobufModule
 * @author xenoblade
 * @since 1.0.0
 */
public class CustomProtobufModule extends Module {

    private final ProtobufJacksonConfig config;

    public CustomProtobufModule() {
        this(ProtobufJacksonConfig.builder().build());
    }

    /** @deprecated */
    @Deprecated
    public CustomProtobufModule(ExtensionRegistry extensionRegistry) {
        this(ProtobufJacksonConfig.builder().extensionRegistry(extensionRegistry).build());
    }

    public CustomProtobufModule(ProtobufJacksonConfig config) {
        this.config = config;
    }

    public String getModuleName() {
        return "ProtobufModule";
    }

    public Version version() {
        return Version.unknownVersion();
    }

    public void setupModule(SetupContext context) {
        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(new CustomMessageSerializer(this.config));
        serializers.addSerializer(new DurationSerializer());
        serializers.addSerializer(new FieldMaskSerializer());
        serializers.addSerializer(new ListValueSerializer());
        serializers.addSerializer(new NullValueSerializer());
        serializers.addSerializer(new StructSerializer());
        serializers.addSerializer(new TimestampSerializer());
        serializers.addSerializer(new ValueSerializer());
        serializers.addSerializer(new CustomWrappedPrimitiveSerializer(DoubleValue.class));
        serializers.addSerializer(new CustomWrappedPrimitiveSerializer(FloatValue.class));
        serializers.addSerializer(new CustomWrappedPrimitiveSerializer(Int64Value.class));
        serializers.addSerializer(new CustomWrappedPrimitiveSerializer(UInt64Value.class));
        serializers.addSerializer(new CustomWrappedPrimitiveSerializer(Int32Value.class));
        serializers.addSerializer(new CustomWrappedPrimitiveSerializer(UInt32Value.class));
        serializers.addSerializer(new CustomWrappedPrimitiveSerializer(BoolValue.class));
        serializers.addSerializer(new CustomWrappedPrimitiveSerializer(StringValue.class));
        serializers.addSerializer(new CustomWrappedPrimitiveSerializer(BytesValue.class));
        context.addSerializers(serializers);
        context.addDeserializers(new MessageDeserializerFactory(this.config));
        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Duration.class, new DurationDeserializer());
        deserializers.addDeserializer(FieldMask.class, new FieldMaskDeserializer());
        deserializers.addDeserializer(ListValue.class, (new ListValueDeserializer()).buildAtEnd());
        deserializers.addDeserializer(NullValue.class, new NullValueDeserializer());
        deserializers.addDeserializer(Struct.class, (new StructDeserializer()).buildAtEnd());
        deserializers.addDeserializer(Timestamp.class, new TimestampDeserializer());
        deserializers.addDeserializer(Value.class, (new ValueDeserializer()).buildAtEnd());
        deserializers.addDeserializer(DoubleValue.class, wrappedPrimitiveDeserializer(DoubleValue.class));
        deserializers.addDeserializer(FloatValue.class, wrappedPrimitiveDeserializer(FloatValue.class));
        deserializers.addDeserializer(Int64Value.class, wrappedPrimitiveDeserializer(Int64Value.class));
        deserializers.addDeserializer(UInt64Value.class, wrappedPrimitiveDeserializer(UInt64Value.class));
        deserializers.addDeserializer(Int32Value.class, wrappedPrimitiveDeserializer(Int32Value.class));
        deserializers.addDeserializer(UInt32Value.class, wrappedPrimitiveDeserializer(UInt32Value.class));
        deserializers.addDeserializer(BoolValue.class, wrappedPrimitiveDeserializer(BoolValue.class));
        deserializers.addDeserializer(StringValue.class, wrappedPrimitiveDeserializer(StringValue.class));
        deserializers.addDeserializer(BytesValue.class, wrappedPrimitiveDeserializer(BytesValue.class));
        context.addDeserializers(deserializers);
        context.setMixInAnnotations(MessageOrBuilder.class, CustomProtobufModule.MessageOrBuilderMixin.class);
    }

    private static <T extends Message> JsonDeserializer<T> wrappedPrimitiveDeserializer(Class<T> type) {
        return (new WrappedPrimitiveDeserializer(type)).buildAtEnd();
    }

    @JsonAutoDetect(
            getterVisibility = Visibility.NONE,
            isGetterVisibility = Visibility.NONE,
            setterVisibility = Visibility.NONE,
            creatorVisibility = Visibility.NONE,
            fieldVisibility = Visibility.NONE
    )
    private static class MessageOrBuilderMixin {
        private MessageOrBuilderMixin() {
        }
    }

}
