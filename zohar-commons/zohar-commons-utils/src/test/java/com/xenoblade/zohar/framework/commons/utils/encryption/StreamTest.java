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
package com.xenoblade.zohar.framework.commons.utils.encryption;

import com.xenoblade.zohar.framework.commons.utils.encryption.factory.KeyFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Security;

import static junit.framework.TestCase.assertTrue;

/**
 * StreamTest
 * @author xenoblade
 * @since 1.0.0
 */
public class StreamTest {

    private static final String FILENAME = "test_picture.jpg";
    private static final String FILENAME_ENCRYPTED = "test_picture.jpg.encrypted";
    private static final String FILENAME_DECRYPTED = "test_picture_stream.jpg";
    private static final int AES_IV_SIZE = 16;
    private static final int DES_IV_SIZE = 8;
    private static final int BUFFER_SIZE = 4 * 1024;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test public void testAES_CTR() throws GeneralSecurityException, IOException {
        Key key = KeyFactory.AES.randomKey();
        Encryptor encryptor = new Encryptor(key, "AES/CTR/NoPadding", AES_IV_SIZE);
        encryptor.setAlgorithmProvider("BC");

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(getFilePathFromResource(FILENAME));
            os = encryptor.wrapOutputStream(new FileOutputStream(getFilePathFromResource(FILENAME_ENCRYPTED)));
            byte[] buffer = new byte[BUFFER_SIZE];
            int nRead;
            while((nRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, nRead);
            }
            os.flush();
        } finally {
            if(is != null) {
                is.close();
            }
            if(os != null) {
                os.close();
            }
        }

        try {
            encryptor = new Encryptor(key, "AES/CTR/NoPadding", AES_IV_SIZE);
            encryptor.setAlgorithmProvider("BC");
            is = encryptor.wrapInputStream(new FileInputStream(getFilePathFromResource(FILENAME_ENCRYPTED)));
            os = new FileOutputStream(getFilePathFromResource(FILENAME_DECRYPTED));
            byte[] buffer = new byte[BUFFER_SIZE];
            int nRead;
            while((nRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, nRead);
            }
            os.flush();
        } finally {
            if(is != null) {
                is.close();
            }
            if(os != null) {
                os.close();
            }
        }

        assertTrue(FileUtils.contentEquals(new File(getFilePathFromResource(FILENAME)), new File(getFilePathFromResource(FILENAME_DECRYPTED))));
    }

    @Test public void testDES_CTR() throws GeneralSecurityException, IOException {
        Key key = KeyFactory.DES.randomKey();
        Encryptor encryptor = new Encryptor(key, "DES/CTR/NoPadding", DES_IV_SIZE);
        encryptor.setAlgorithmProvider("BC");

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(getFilePathFromResource(FILENAME));
            os = encryptor.wrapOutputStream(new FileOutputStream(getFilePathFromResource(FILENAME_ENCRYPTED)));
            byte[] buffer = new byte[BUFFER_SIZE];
            int nRead;
            while((nRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, nRead);
            }
            os.flush();
        } finally {
            if(is != null) {
                is.close();
            }
            if(os != null) {
                os.close();
            }
        }

        try {
            encryptor = new Encryptor(key, "DES/CTR/NoPadding", DES_IV_SIZE);
            encryptor.setAlgorithmProvider("BC");
            is = encryptor.wrapInputStream(new FileInputStream(getFilePathFromResource(FILENAME_ENCRYPTED)));
            os = new FileOutputStream(getFilePathFromResource(FILENAME_DECRYPTED));
            byte[] buffer = new byte[BUFFER_SIZE];
            int nRead;
            while((nRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, nRead);
            }
            os.flush();
        } finally {
            if(is != null) {
                is.close();
            }
            if(os != null) {
                os.close();
            }
        }

        assertTrue(FileUtils.contentEquals(new File(getFilePathFromResource(FILENAME)), new File(getFilePathFromResource(FILENAME_DECRYPTED))));
    }

    private String getFilePathFromResource(String resourcePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL relativePath = classLoader.getResource("");
        String absolutePath = relativePath.getPath() + resourcePath;
        return absolutePath;
    }

}
