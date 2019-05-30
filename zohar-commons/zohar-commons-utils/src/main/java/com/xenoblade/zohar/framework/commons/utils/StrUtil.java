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
package com.xenoblade.zohar.framework.commons.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import com.xenoblade.zohar.framework.commons.utils.support.EEncodeType;
import com.xenoblade.zohar.framework.commons.utils.support.EHashType;
import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * StrUtil
 * @author xenoblade
 * @since 1.0.0
 */
@UtilityClass
public class StrUtil {

    @Nullable
    public static byte[] encode(byte[] bytes, EEncodeType encodeType) {
        if (bytes.length == 0) {
            return null;
        }
        byte[] encodeBytes = Arrays.copyOf(bytes, bytes.length);
        switch (encodeType) {
            case BASE64:
            {
                encodeBytes = Base64.encode(bytes, false);
                break;
            }
            case HEX:
            {
                String encodeStr = HexUtil.encodeHexStr(bytes);
                encodeBytes = encodeStr.getBytes();
                break;
            }
        }
        return encodeBytes;
    }

    @Nullable
    public static byte[] hash(byte[] bytes, EHashType hashType) {
        if (bytes.length == 0) {
            return null;
        }
        byte[] hashBytes = Arrays.copyOf(bytes, bytes.length);
        switch (hashType) {
            case MD5: {
                // md5 + hex
                byte[] hash = SecureUtil.md5().digest(bytes);
                hashBytes = encode(hash, EEncodeType.HEX);
                break;
            }
            case SHA1: {
                // sha-1 + hex
                byte[] hash = SecureUtil.sha1().digest(bytes);
                hashBytes = encode(hash, EEncodeType.HEX);
                break;
            }
            case HMAC_SHA1: {
                // hmac + sha1
                byte[] hash = SecureUtil.hmacSha1().digest(bytes);
                hashBytes = encode(hash, EEncodeType.HEX);
                break;
            }
            case HMAC_MD5: {
                // hmac + sha1
                byte[] hash = SecureUtil.hmacMd5().digest(bytes);
                hashBytes = encode(hash, EEncodeType.HEX);
                break;
            }

        }
        return hashBytes;
    }

}
