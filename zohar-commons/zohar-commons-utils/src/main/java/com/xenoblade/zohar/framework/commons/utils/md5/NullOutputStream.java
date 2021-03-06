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

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Type NullOutputStream
 * @Desc NullOutputStream
 * @author dingming
 * @date 2019年07月31日 1:59 PM
 * @version
 */
public class NullOutputStream extends OutputStream {

    private boolean closed = false;

    public NullOutputStream() {
    }

    public void close() {
        this.closed = true;
    }

    public void flush() throws IOException {
        if (this.closed)
            _throwClosed();
    }

    private void _throwClosed() throws IOException {
        throw new IOException("This OutputStream has been closed");
    }

    public void write(byte[] b) throws IOException {
        if (this.closed)
            _throwClosed();
    }

    public void write(byte[] b, int offset, int len) throws IOException {
        if (this.closed)
            _throwClosed();
    }

    public void write(int b) throws IOException {
        if (this.closed)
            _throwClosed();
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 *
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2019年07月31日 1:59 PM dingming create
 */
