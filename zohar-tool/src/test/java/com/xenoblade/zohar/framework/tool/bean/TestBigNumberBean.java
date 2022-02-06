/*
 * Copyright [2022] [xenoblade]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.tool.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * TestBigNumberBean
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class TestBigNumberBean implements Serializable {

    private static final long serialVersionUID = 3605314467288137245L;

    private BigDecimal aBigDecimal = new BigDecimal("3.12435");

    private Long aLong = Long.valueOf(343254356884645753L);

    private long anotherLong = Long.valueOf(4796904892307445488L);

    public BigDecimal getaBigDecimal() {
        return aBigDecimal;
    }

    public void setaBigDecimal(BigDecimal aBigDecimal) {
        this.aBigDecimal = aBigDecimal;
    }

    public Long getaLong() {
        return aLong;
    }

    public long getAnotherLong() {
        return anotherLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }

    public void setAnotherLong(long anotherLong) {
        this.anotherLong = anotherLong;
    }
}