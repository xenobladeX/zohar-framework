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
package com.xenoblade.zohar.framework.security.basic.config;

import com.xenoblade.zohar.framework.security.basic.support.BasicAuthenticationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;

/**
 * BasicSecurityAutoConfiguration
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(BasicSecurityProperties.class)
public class BasicSecurityAutoConfiguration {

    @Autowired
    private BasicSecurityProperties basicSecurityProperties;

    @Configuration
    @Order(1)
    public class RootWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .formLogin()
                    .loginPage("/authentication/require")
                    .loginProcessingUrl(basicSecurityProperties.getFormLogin().getLoginProcessingUrl())
                    .and()
                    .authorizeRequests()
                    .antMatchers("/authentication/require", basicSecurityProperties.getFormLogin().getLoginPage()).permitAll()
                    .anyRequest().authenticated()
                    .and()
                    // spring security 默认要求 post 请求带 token，禁用 CSRF
                    .csrf().disable();

            initDefaultLoginFilter(http);
        }

    }

    private void initDefaultLoginFilter(HttpSecurity http) throws Exception{
        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = new DefaultLoginPageGeneratingFilter();
        loginPageGeneratingFilter.setFormLoginEnabled(true);
        loginPageGeneratingFilter.setUsernameParameter(basicSecurityProperties.getFormLogin().getUsernameParameter());
        loginPageGeneratingFilter.setPasswordParameter(basicSecurityProperties.getFormLogin().getPasswordParameter());
        loginPageGeneratingFilter.setLoginPageUrl(basicSecurityProperties.getFormLogin().getLoginPage());
        loginPageGeneratingFilter.setFailureUrl(basicSecurityProperties.getFormLogin().getFailureUrl());
        loginPageGeneratingFilter.setAuthenticationUrl(basicSecurityProperties.getFormLogin().getLoginProcessingUrl());
        http.addFilter(loginPageGeneratingFilter);
    }


    @Bean
    public BasicAuthenticationController basicAuthenticationController() {
        return new BasicAuthenticationController();
    }

}
