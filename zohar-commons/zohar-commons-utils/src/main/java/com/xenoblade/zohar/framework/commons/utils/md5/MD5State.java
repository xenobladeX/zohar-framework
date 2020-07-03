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

import java.io.Serializable;

/**
 * @Type MD5State
 * @Desc MD5State
 * @author dingming
 * @date 2019年07月31日 2:00 PM
 * @version
 */
public class MD5State implements Serializable {

    private static final long serialVersionUID = -3110076164943228256L;

    /**
     * 128-bit state
     */
    int state[];

    /**
     * 64-bit character count
     */
    long count;

    /**
     * 64-byte buffer (512 bits) for storing to-be-hashed characters
     */
    byte buffer[];

    public MD5State() {
        buffer = new byte[64];
        count = 0;
        state = new int[4];

        state[0] = 0x67452301;
        state[1] = 0xefcdab89;
        state[2] = 0x98badcfe;
        state[3] = 0x10325476;

    }

    /** Create this State as a copy of another state */
    public MD5State(MD5State from) {
        this();

        int i;

        for (i = 0; i < buffer.length; i++)
            this.buffer[i] = from.buffer[i];

        for (i = 0; i < state.length; i++)
            this.state[i] = from.state[i];

        this.count = from.count;
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 *
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2019年07月31日 2:00 PM dingming create
 */
