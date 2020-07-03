/*
 * Project: homecloud-parent
 *
 * File Created at 2019年07月31日
 *
 * Copyright 2018 CMCC Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ZYHY Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
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
