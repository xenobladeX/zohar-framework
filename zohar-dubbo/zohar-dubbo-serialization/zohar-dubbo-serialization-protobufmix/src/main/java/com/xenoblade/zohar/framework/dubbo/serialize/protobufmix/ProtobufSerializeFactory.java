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

import java.io.IOException;

import com.google.protobuf.MessageLite;

/**
 * Protocol buffer serialize factory. singleton.
 * @author xenoblade
 * @since 1.0.0
 */
public class ProtobufSerializeFactory implements SpecialSerializeFactory {

    public static final ProtobufSerializeFactory INSTANCE = new ProtobufSerializeFactory();

    @Override
    public boolean supportDeserialize(int type) {
        if (type == ProtobufUtil.TYPE_PROTOBUF) {
            return true;
        }
        return false;
    }

    @Override
    public Object parse(Hessian2WithSpecialObjectInput input) throws IOException {
        String className = input.readUTF();
        byte[] data = input.readBytes();

        return ProtobufUtil.parseFrom(className, data);
    }

    @Override
    public boolean trySerializeObject(Hessian2WithSpecialObjectOutput output, Object obj)
            throws IOException {
        int type = ProtobufUtil.TYPE_NORMAL;
        boolean isMessageLiteBuilder = false;
        if (obj instanceof MessageLite.Builder) {
            isMessageLiteBuilder = true;
            type = ProtobufUtil.TYPE_PROTOBUF;
        } else if (obj instanceof MessageLite) {
            type = ProtobufUtil.TYPE_PROTOBUF;
        }

        if (type == ProtobufUtil.TYPE_PROTOBUF) {
            // write tag big for protocol buffer
            output.writeInt(ProtobufUtil.TYPE_PROTOBUF);
            // write protocol buffer class name
            output.writeUTF(obj.getClass().getName());
            if (isMessageLiteBuilder) {
                output.writeBytes(((MessageLite.Builder) obj).build().toByteArray());
            } else {
                output.writeBytes(((MessageLite) obj).toByteArray());
            }

            return true;
        }

        return false;
    }

}
