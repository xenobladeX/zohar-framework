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

import com.xenoblade.zohar.framework.commons.web.converter.EntityMessageConverterProcessor;
import com.xenoblade.zohar.framework.commons.web.converter.MessageConverterProcessor;
import com.xenoblade.zohar.framework.commons.web.converter.MethodReturnValueHandler;
import com.xenoblade.zohar.framework.commons.web.converter.ResponseBodyMessageConverterProcessor;
import com.xenoblade.zohar.framework.security.basic.handler.BasicAuthenticationFailureHandler;
import com.xenoblade.zohar.framework.security.basic.handler.BasicAuthenticationSuccessHandler;
import com.xenoblade.zohar.framework.security.basic.handler.DefaultLoginExceptionConverter;
import com.xenoblade.zohar.framework.security.basic.handler.DefaultLoginResponseConverter;
import com.xenoblade.zohar.framework.security.basic.handler.LoginExceptionConverter;
import com.xenoblade.zohar.framework.security.basic.handler.LoginResponseConverter;
import com.xenoblade.zohar.framework.security.basic.support.BasicAuthenticationController;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.web.accept.ContentNegotiationManager;

import java.util.List;

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

    @Autowired
    private HttpMessageConverters httpMessageConverters;

    @Autowired
    private ContentNegotiationManager contentNegotiationManager;


    @AllArgsConstructor
    @Configuration
    @Order(1)
    public class RootWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        private AuthenticationSuccessHandler authenticationSuccessHandler;

        private AuthenticationFailureHandler authenticationFailureHandler;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .formLogin()
                    .loginPage("/authentication/require")
                    .loginProcessingUrl(basicSecurityProperties.getFormLogin().getLoginProcessingUrl())
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/authentication/require", basicSecurityProperties.getFormLogin().getLoginPage()).permitAll()
                    .anyRequest().authenticated()
                    .and()
                    // spring security 默认要求 post 请求带 token，禁用 CSRF
                    .csrf().disable();

            initDefaultLoginFilter(http);
        }

        @Override public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/css/**");
        }
    }

    private void initDefaultLoginFilter(HttpSecurity http) {
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
    @ConditionalOnMissingBean(LoginResponseConverter.class)
    public LoginResponseConverter defaultLoginResponseConverter() {
        DefaultLoginResponseConverter defaultLoginResponseConverter = new DefaultLoginResponseConverter();

        return defaultLoginResponseConverter;
    }

    @Bean
    @ConditionalOnMissingBean(LoginExceptionConverter.class)
    public LoginExceptionConverter defaultLoginExceptionConverter() {
        DefaultLoginExceptionConverter defaultLoginExceptionConverter = new DefaultLoginExceptionConverter();

        return defaultLoginExceptionConverter;
    }

    @Bean
    public MethodReturnValueHandler entityMessageConverterProcessor(HttpMessageConverters httpMessageConverters, ContentNegotiationManager contentNegotiationManager) {
        MethodReturnValueHandler entityMessageConverterProcessor = new EntityMessageConverterProcessor(httpMessageConverters.getConverters(), contentNegotiationManager);
        return entityMessageConverterProcessor;
    }

    @Bean
    public MethodReturnValueHandler responseBodyMessageConverterProcessor(HttpMessageConverters httpMessageConverters, ContentNegotiationManager contentNegotiationManager) {
        MethodReturnValueHandler entityMessageConverterProcessor = new ResponseBodyMessageConverterProcessor(httpMessageConverters.getConverters(), contentNegotiationManager);
        return entityMessageConverterProcessor;
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    public AuthenticationSuccessHandler authenticationSuccessHandler(LoginResponseConverter loginResponseConverter, List<MethodReturnValueHandler> methodReturnValueHandlerList) {
        BasicAuthenticationSuccessHandler basicAuthenticationSuccessHandler = new BasicAuthenticationSuccessHandler();
        basicAuthenticationSuccessHandler.setLoginType(basicSecurityProperties.getFormLogin().getLoginType());
        basicAuthenticationSuccessHandler.setLoginResponseConverter(loginResponseConverter);
        basicAuthenticationSuccessHandler.setMethodReturnValueHandlerList(methodReturnValueHandlerList);
        return basicAuthenticationSuccessHandler;
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler authenticationFailureHandler(LoginExceptionConverter loginResponseConverter, List<MethodReturnValueHandler> methodReturnValueHandlerList) {
        BasicAuthenticationFailureHandler basicAuthenticationSuccessHandler = new BasicAuthenticationFailureHandler();
        basicAuthenticationSuccessHandler.setLoginType(basicSecurityProperties.getFormLogin().getLoginType());
        basicAuthenticationSuccessHandler.setLoginExceptionConverter(loginResponseConverter);
        basicAuthenticationSuccessHandler.setMethodReturnValueHandlerList(methodReturnValueHandlerList);
        return basicAuthenticationSuccessHandler;
    }


    @Bean
    public BasicAuthenticationController basicAuthenticationController() {
        return new BasicAuthenticationController();
    }

}
