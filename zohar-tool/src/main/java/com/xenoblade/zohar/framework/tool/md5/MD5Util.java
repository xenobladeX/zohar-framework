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
package com.xenoblade.zohar.framework.tool.md5;


import com.xenoblade.zohar.framework.core.api.dto.BasicErrorCode;
import com.xenoblade.zohar.framework.core.api.exception.SysException;

import java.io.*;

/**
 * MD5Util
 * @author xenoblade
 * @since 1.0.0
 */
public final class MD5Util {
    public static String computeMD5Hex(InputStream is, int bufferLength) throws SysException {
        byte[] md5Bytes = compute(is, bufferLength);
        return MD5.asHex(md5Bytes);
    }

    public static String computeMD5Hex(byte[] bytes) throws SysException {
        byte[] md5Bytes = compute(bytes);
        return MD5.asHex(md5Bytes);
    }

    public static String computeMD5Hex(File file, int bufferLength) throws SysException {
        byte[] md5Bytes = compute(file, bufferLength);
        return MD5.asHex(md5Bytes);
    }

    private static byte[] compute(byte[] bytes) throws SysException {
        MD5 md5 = new MD5();
        md5.Update(bytes);
        return md5.Final();
    }

    private static byte[] compute(File file, int bufferLength) throws SysException {
        FileInputStream in = null;
        Object var3 = null;

        byte[] md5Bytes;
        try {
            in = new FileInputStream(file);
            md5Bytes = compute((InputStream)in, bufferLength);
        } catch (IOException var12) {
            throw new SysException(BasicErrorCode.FILE_READ_FAILED);
        } finally {
            try {
                in.close();
            } catch (IOException var11) {
            }

        }

        return md5Bytes;
    }

    private static byte[] compute(InputStream inputStream, int bufferLength) throws SysException {
        if (bufferLength < 1) {
            bufferLength = 2048;
        }

        MD5 md5 = new MD5();
        byte[] buffer = new byte[bufferLength];

        try {
            for(int read = inputStream.read(buffer, 0, bufferLength); read > -1; read = inputStream.read(buffer, 0, bufferLength)) {
                md5.Update(buffer, 0, read);
            }

            return md5.Final();
        } catch (IOException var5) {
            throw new SysException(BasicErrorCode.FILE_READ_FAILED);
        }
    }

    public static MD5 compute(String filePath, long offset, int bufferLength) {
        RandomAccessFile file = null;

        try {
            file = new RandomAccessFile(filePath, "rw");
        } catch (IOException var18) {
            throw new SysException(BasicErrorCode.FILE_READ_FAILED);
        }

        try {
            file.seek(offset);
            MD5 md5 = new MD5();
            byte[] buffer = new byte[bufferLength];

            int numRead;
            do {
                numRead = file.read(buffer);
                if (numRead > 0) {
                    md5.Update(buffer, numRead);
                }
            } while(numRead != -1);

            MD5 var8 = md5;
            return var8;
        } catch (Exception var19) {
            throw new SysException(BasicErrorCode.FILE_READ_FAILED);
        } finally {
            try {
                file.close();
            } catch (IOException var17) {
            }

        }
    }

    private MD5Util() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
