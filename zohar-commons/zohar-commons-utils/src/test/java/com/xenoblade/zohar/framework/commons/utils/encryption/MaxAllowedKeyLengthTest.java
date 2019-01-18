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

import org.junit.Test;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;

/**
 * <p>Tests whether the Java Cryptography Extension (JCE) unlimited strength jurisdiction policy files have been installed.</p>
 * @author xenoblade
 * @since 1.0.0
 */
public class MaxAllowedKeyLengthTest {

    @Test public void maxAllowedKeyLengthTest() throws GeneralSecurityException {
        String[] algorithms = new String[] { "AES", "DES", "RSA", "Blowfish", "RC2", "RC4", "RC5", "ARC4"};
        for(String algorithm: algorithms) {
            int maxKeyLength = Cipher.getMaxAllowedKeyLength(algorithm);
            System.out.println(algorithm + ": " + maxKeyLength);
            assertEquals(Integer.MAX_VALUE, maxKeyLength);
        }
    }

}
