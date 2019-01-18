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

import com.xenoblade.zohar.framework.commons.utils.encryption.factory.AsymmetricalEncryptorFactory;
import com.xenoblade.zohar.framework.commons.utils.encryption.factory.KeyPairFactory;
import com.xenoblade.zohar.framework.commons.utils.encryption.factory.SimpleKeyPair;
import com.xenoblade.zohar.framework.commons.utils.encryption.util.FileEncryptor;
import com.xenoblade.zohar.framework.commons.utils.encryption.util.TextEncryptor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * UtilTest
 * @author xenoblade
 * @since 1.0.0
 */
public class UtilTest {

    private static final String FILENAME = "test_picture.jpg";
    private static final String FILENAME_ENCRYPTED = "test_picture.jpg.encrypted";
    private static final String FILENAME_DECRYPTED = "test_picture_util.jpg";

    @Test public void testEncryptedFile() throws GeneralSecurityException, IOException {
        File srcFile = new File(getFilePathFromResource(FILENAME));
        File encryptedFile = new File(getFilePathFromResource(FILENAME_ENCRYPTED));
        File decryptedFile = new File(getFilePathFromResource(FILENAME_DECRYPTED));
        FileEncryptor fe = new FileEncryptor();
        fe.encrypt(srcFile, encryptedFile);
        fe.decrypt(encryptedFile, decryptedFile);
        assertTrue(FileUtils.contentEquals(srcFile, decryptedFile));
    }

    @Test public void testEncryptedText() throws GeneralSecurityException {
        String message = "This is a short test message";
        TextEncryptor te = new TextEncryptor();
        String encrypted = te.encrypt(message);
        String decrypted = te.decrypt(encrypted);
        assertTrue(decrypted.equals(message));
    }

    private String getFilePathFromResource(String resourcePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL relativePath = classLoader.getResource("");
        String absolutePath = relativePath.getPath() + resourcePath;
        return absolutePath;
    }

    @Test
    public void testRSA() throws Exception{
        SimpleKeyPair keyPair = KeyPairFactory.RSA.randomKeyPair();
        AsymmetricalEncryptor rsaEncryptor = AsymmetricalEncryptorFactory.RSA.messageEncryptor(keyPair);

        String message = "This is a short test message";
        byte[] encrypted = rsaEncryptor.encryptPublic(message.getBytes());
        byte[] decrypted = rsaEncryptor.decryptPrivate(encrypted);
        assertEquals(message, new String(decrypted));
    }

    @Test
    public void testRSAWithExistKeyPair() throws Exception{
        String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD0+WtsVBtSS3p6I8Y9lfG+MJn3go834yS8w1MJ+KJo3fdB17uS4U5E7/8dJR+MacxorljqlDegAmcMqzQ7TIra7poLWyf3AhIbkOk3k+/+Gxj4JiERMtjEtRDo2AJ7+aIHs8qH1pR/1rQjrickRxclcLfzDR94w2hZIuRQrt498QIDAQAB";
        String RSA_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPT5a2xUG1JLenojxj2V8b4wmfeCjzfjJLzDUwn4omjd90HXu5LhTkTv/x0lH4xpzGiuWOqUN6ACZwyrNDtMitrumgtbJ/cCEhuQ6TeT7/4bGPgmIREy2MS1EOjYAnv5ogezyofWlH/WtCOuJyRHFyVwt/MNH3jDaFki5FCu3j3xAgMBAAECgYA3bhxldZ58/4EmE+QuWThK7ZpZC9y2K5pLb/LhJbYx/k+NQXA66uoX5SoXgSfjUKkk4pZR9dmkRgblMfPumwIr2E3N2DbCNYo5QORcWN6tvBpe4yhHkrz29j/V1SDa79Dphhnx+YwO/oX1lWl3ZMLAqrTDEnnqczxiRpXs6IRqAQJBAPzBuecmqiyoINIKcQS/TFZ+laUwzDbGGimc2CtDaqwmxACluHInTVYvWj6kcRoDUpfTNDDO9w7k9YWuLB/hhKECQQD4HiELytgGG1Sm2JvCW9NMIwIPkQOozUobd//ydi4akvqFCs9d1NIO6Jo2raefdnFEJOIXWxK7oO9y4MQ0FudRAkEA+Qf6nWe4BXXFmI0XG6FLmnDwCikRG+qFfnh9d+rdoC41sZmWKErW5NKU7OFiWknpx5MdefWA8BjgW0znRqYQ4QJAHJGLiIoQmZNoCdYDCTv9dlTJlfVCkJsin0sP42EMKe8mfU8jVVB9502NBCK2nDvNCuWcSsgnvgbnyzhqHv1MIQJAP6OonqftMh0ytZdFHEC9VpmQ7ELqakG/7CpVq+TgyU88YKW+46RBRuiYijYTqbFb6IikgzhbdUUdTA8hZvO6SA==";

        SimpleKeyPair keyPair = KeyPairFactory.RSA.keyPairFromPassword(
                Base64.decodeBase64(RSA_PUBLIC_KEY), Base64.decodeBase64(RSA_PRIVATE_KEY));
        AsymmetricalEncryptor rsaEncryptor = AsymmetricalEncryptorFactory.RSA.messageEncryptor(keyPair);

        String message = "This is a short test message";
        byte[] encrypted = rsaEncryptor.encryptPublic(message.getBytes());
        byte[] decrypted = rsaEncryptor.decryptPrivate(encrypted);
        assertEquals(message, new String(decrypted));
    }

    @Test
    public void testRSAWithLargeData() throws Exception{
        SimpleKeyPair keyPair = KeyPairFactory.RSA.randomKeyPair();
        AsymmetricalEncryptor rsaEncryptor = AsymmetricalEncryptorFactory.RSA.messageEncryptor(keyPair);

        String message = "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                + "This is a long test message"
                ;
        byte[] encrypted = rsaEncryptor.encryptPublic(message.getBytes(), 117);
        byte[] decrypted = rsaEncryptor.decryptPrivate(encrypted, 128);
        assertEquals(message, new String(decrypted));
    }



}
