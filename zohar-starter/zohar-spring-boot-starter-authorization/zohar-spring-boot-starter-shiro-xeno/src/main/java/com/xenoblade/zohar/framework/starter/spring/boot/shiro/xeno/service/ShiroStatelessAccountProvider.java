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
package com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.service;

import org.apache.shiro.authc.AuthenticationException;

import java.util.Set;

/**
 * 安全信息服务，应用系统必须实现这个接口，为安全认证提供必要的信息。
 * @author xenoblade
 * @since 1.0.0
 */
public interface ShiroStatelessAccountProvider {
    /**
     * 检查账号是否正常
     * <br>如果返回false或抛出AuthenticationException则不予通过认证
     * @param appId 客户标识
     */
    public boolean checkAccount(String appId) throws AuthenticationException;
    /**
     * 获取客户端的签名私钥
     * <br>如果客户端没有私钥返回空，则使用全局秘钥
     * @param appId 客户标识
     */
    public String loadAppKey(String appId);
    /**
     * 根据客户标识加载持有角色
     * @param appId 客户标识
     * @return 角色列表
     */
    public Set<String> loadRoles(String appId);
    /**
     * 根据客户标识加载持有权限
     * @param appId 客户标识
     * @return 角色列表
     */
    public Set<String> loadPermissions(String appId);
}
