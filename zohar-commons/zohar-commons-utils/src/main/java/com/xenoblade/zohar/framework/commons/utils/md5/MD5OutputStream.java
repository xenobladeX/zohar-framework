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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Type MD5OutputStream
 * @Desc MD5OutputStream
 * @author dingming
 * @date 2019年07月31日 2:01 PM
 * @version
 */
public class MD5OutputStream extends FilterOutputStream {
    /**
     * MD5 context
     */
    private MD5 md5;

    /**
     * Creates MD5OutputStream
     * @param out	The output stream
     */

    public MD5OutputStream(OutputStream out) {
        super(out);

        md5 = new MD5();
    }

    /**
     * Writes a byte.
     *
     * @see FilterOutputStream
     */

    public void write(int b) throws IOException {
        out.write(b);
        md5.Update((byte) b);
    }

    /**
     * Writes a sub array of bytes.
     *
     * @see FilterOutputStream
     */

    public void write(byte b[], int off, int len) throws IOException {
        out.write(b, off, len);
        md5.Update(b, off, len);
    }

    /**
     * Returns array of bytes representing hash of the stream as finalized
     * for the current state.
     *
     * @return the finalized MD5 bytes
     * @see MD5#Final
     */

    public byte[] hash() {
        return md5.Final();
    }

    public MD5 getMD5() {
        return md5;
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 *
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2019年07月31日 2:01 PM dingming create
 */
