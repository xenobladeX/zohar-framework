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
package com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.config;

import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.service.DefaultAccountProvider;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.service.ShiroCryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

/**
 * 默认配置
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
@ConditionalOnMissingBean(XenoShiroConfigurationAdapter.class)
public class DefaultShiroConfiguration extends XenoShiroConfigurationAdapter{

    @Autowired
    private ShiroCryptoService shiroCryptoService;

    @Override
    protected void configure(SecurityManagerConfig securityManager) {
        System.out.println("欢迎使用： zohar-spring-boot-starter-shiro-xeno");
        System.out.println("已为您创建体验账号： test,密码test");
        DefaultAccountProvider defAccountProvider = new DefaultAccountProvider();
        defAccountProvider.setShiroCryptoService(shiroCryptoService);
        securityManager.setAccountProvider(defAccountProvider);
    }

    @Override
    protected void configure(FilterChainConfig filterChain) {

    }

}
