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
