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
package com.xenoblade.zohar.framework.commons.utils.encryption.factory;

import com.xenoblade.zohar.framework.commons.utils.encryption.Encryptor;

import java.security.Key;

/**
 * <p>Factory interface for generating keys.</p>
 * @author xenoblade
 * @since 1.0.0
 */
public interface EncryptorFactory {

    /**
     * AES <code>EncryptorFactory</code> implementation
     */
    EncryptorFactory AES = new AESEncryptorFactory();
    /**
     * DES <code>EncryptorFactory</code> implementation
     */
    EncryptorFactory DES = new DESEncryptorFactory();

    /**
     * <p>Returns an <code>Encryptor</code> instance usable for message encryption.</p>
     * @param key
     * @return
     */
    Encryptor messageEncryptor(Key key);

    /**
     * <p>Returns an <code>Encryptor</code> instance usable for stream encryption.</p>
     * @param key
     * @return
     */
    Encryptor streamEncryptor(Key key);

}
