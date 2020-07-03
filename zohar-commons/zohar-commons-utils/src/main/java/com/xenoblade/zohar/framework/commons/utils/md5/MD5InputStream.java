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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Type MD5InputStream
 * @Desc MD5InputStream
 * @author dingming
 * @date 2019年07月31日 2:02 PM
 * @version
 */
public class MD5InputStream extends FilterInputStream {
    /**
     * MD5 context
     */
    private MD5 md5;

    /**
     * Creates a MD5InputStream
     * @param in	The input stream
     */
    public MD5InputStream(InputStream in) {
        super(in);

        md5 = new MD5();
    }

    /**
     * Read a byte of data.
     * @see FilterInputStream
     */
    public int read() throws IOException {
        int c = in.read();

        if (c == -1)
            return -1;

        if ((c & ~0xff) != 0) {
            System.out.println("MD5InputStream.read() got character with (c & ~0xff) != 0)!");
        } else {
            md5.Update(c);
        }

        return c;
    }

    /**
     * Reads into an array of bytes.
     *
     * @see FilterInputStream
     */
    public int read(byte bytes[], int offset, int length) throws IOException {
        int r;

        if ((r = in.read(bytes, offset, length)) == -1)
            return r;

        md5.Update(bytes, offset, r);

        return r;
    }

    /**
     * Returns array of bytes representing hash of the stream as
     * finalized for the current state.
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
 * 2019年07月31日 2:02 PM dingming create
 */
