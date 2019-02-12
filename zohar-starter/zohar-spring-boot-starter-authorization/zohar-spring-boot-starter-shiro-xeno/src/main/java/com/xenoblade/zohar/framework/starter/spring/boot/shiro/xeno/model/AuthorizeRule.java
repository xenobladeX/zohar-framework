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
package com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.model;

import com.xenoblade.zohar.framework.commons.api.bean.Bean;

/**
 * 权限验证规则
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class AuthorizeRule implements Bean{

    private static final long serialVersionUID = 1L;

    public static final short RULE_TYPE_DEF = 1;
    public static final short RULE_TYPE_HMAC = 2;
    public static final short RULE_TYPE_JWT = 3;
    public static final short RULE_TYPE_CUSTOM = 4;

    private short type;// 规则类型

    public short getType() {
        return type;
    }
    public void setType(short type) {
        this.type = type;
    }

    public abstract StringBuilder toFilterChain();
}
