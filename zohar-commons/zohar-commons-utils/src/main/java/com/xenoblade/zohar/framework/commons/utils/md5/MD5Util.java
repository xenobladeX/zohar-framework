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
package com.xenoblade.zohar.framework.commons.utils.md5;

import com.xenoblade.zohar.framework.commons.api.enums.BasicZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * MD5Util
 * @author xenoblade
 * @since 1.0.0
 */
@UtilityClass
public class MD5Util {

    public String computeMD5Hex(InputStream is, int bufferLength) throws ZoharException {

        byte[] md5Bytes = compute(is, bufferLength);

        return MD5.asHex(md5Bytes);
    }

    public String computeMD5Hex(byte[] bytes) throws ZoharException {

        byte[] md5Bytes = compute(bytes);

        return MD5.asHex(md5Bytes);
    }

    public String computeMD5Hex(File file, int bufferLength) throws ZoharException {
        byte[] md5Bytes = compute(file, bufferLength);

        return MD5.asHex(md5Bytes);
    }

    private byte[] compute(byte[] bytes) throws ZoharException {
        MD5 md5 = new MD5();
        md5.Update(bytes);
        return md5.Final();
    }

    private byte[] compute(File file, int bufferLength) throws ZoharException {

        FileInputStream in = null;

        byte[] md5Bytes = null;
        try {
            in = new FileInputStream(file);
            md5Bytes = compute(in, bufferLength);
        } catch (IOException ex) {
            throw new ZoharException(BasicZoharErrorCode.FILE_READ_FAILED);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {

            }
        }

        return md5Bytes;

    }

    private byte[] compute(InputStream inputStream, int bufferLength) throws ZoharException {
        if (bufferLength < 1) {
            bufferLength = 2048;
        }

        MD5 md5 = new MD5();
        byte[] buffer = new byte[bufferLength];

        try {
            for (int read = inputStream.read(buffer, 0, bufferLength); read > -1; read = inputStream.read(buffer, 0,
                    bufferLength)) {
                md5.Update(buffer, 0, read);
            }

            return md5.Final();
        } catch (IOException ex) {
            throw new ZoharException(BasicZoharErrorCode.FILE_READ_FAILED);
        }
    }

    public MD5 compute(String filePath, long offset, int bufferLength) {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(filePath, "rw");
        } catch (IOException ex) {
            throw new ZoharException(BasicZoharErrorCode.FILE_READ_FAILED);
        }

        try {
            file.seek(offset);
            MD5 md5 = new MD5();
            // update md5
            byte[] buffer = new byte[bufferLength];
            int numRead;
            do {
                numRead = file.read(buffer);
                if (numRead > 0) {
                    md5.Update(buffer, numRead);
                }
            } while (numRead != -1);
            return md5;
        } catch (Exception ex) {
            throw new ZoharException(BasicZoharErrorCode.FILE_READ_FAILED);
        } finally {
            try {
                file.close();
            } catch (IOException ex) {

            }
        }

    }
    
}
