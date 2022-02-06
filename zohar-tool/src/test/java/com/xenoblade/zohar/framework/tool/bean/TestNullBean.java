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

/**
 * TestNull
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class TestNullBean implements Serializable {

    private static final long serialVersionUID = -2658019832243816331L;

    private String aNull;

    private String aNotNull = "not null";

    public String getaNotNull() {
        return aNotNull;
    }

    public String getaNull() {
        return aNull;
    }

    public void setaNotNull(String aNotNull) {
        this.aNotNull = aNotNull;
    }

    public void setaNull(String aNull) {
        this.aNull = aNull;
    }
}