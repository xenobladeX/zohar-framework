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
package com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.authc;

import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.model.Account;

/**
 * 无状态验证本地缓存
 * <br>由于无SESSION,账号信息缓存于此供应用使用
 * @author xenoblade
 * @since 1.0.0
 */
public abstract class StatelessLocals {

    private static final ThreadLocal<Account> ACCOUNTS = new ThreadLocal<Account>();

    public static Account getAccount() {
        return ACCOUNTS.get();
    }

    protected static void setAccount(Account account) {
        ACCOUNTS.set(account);
    }

    protected static void removeAccount() {
        ACCOUNTS.remove();
    }

}
