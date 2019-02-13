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

import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.service.ShiroCryptoService;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.service.ShiroSecurityService;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * shiro自动配置
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
@Import(DefaultShiroConfiguration.class)
@AutoConfigureAfter(AbstractCachingConfiguration.class)
@ConditionalOnProperty(prefix = "zohar.shiro.xeno", name = "enable", havingValue = "true", matchIfMissing = true)
public class XenoShiroAutoConfiguration {

    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private ShiroProperties properties;

    @Bean
    public BeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public ShiroCryptoService shiroCryptoService() {
        return new ShiroCryptoService();
    }

    @Bean
    public XenoShiroManager xenoShiroManager(ShiroCryptoService shiroCryptoService) {
        XenoShiroManager shiroManager = new XenoShiroManager(
                this.beanFactory
                ,this.properties
                ,new SecurityManagerConfig()
                ,new FilterChainConfig());
        shiroManager.setCryptoService(shiroCryptoService);
        return shiroManager;
    }

    @Bean
    public ShiroSecurityService shiroSecurityService() {
        return new ShiroSecurityService();
    }
}
