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
package com.xenoblade.zohar.framework.log.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.xenoblade.zohar.framework.commons.utils.jackson.JacksonUtil;
import com.xenoblade.zohar.framework.commons.log.core.DefaultAccessLoggerParser;
import com.xenoblade.zohar.framework.commons.log.core.aop.AccessLoggerAdvisor;
import com.xenoblade.zohar.framework.commons.log.core.aop.AccessLoggerInterceptor;
import com.xenoblade.zohar.framework.commons.log.core.config.AccessLoggerInterceptorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * LogAutoConfiguration
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "zohar.log.access", name = "enable", havingValue = "true", matchIfMissing = true)
public class AccessLoggerAutoConfiguration {


    private List<AccessLoggerConfigurer> configurers = Collections.emptyList();


    @Autowired(required = false)
    public void setConfigurers(List<AccessLoggerConfigurer> configurers) {
        this.configurers = configurers;
    }

    @Bean
    public AccessLoggerAdvisor accessLoggerAdvisor(ApplicationEventPublisher eventPublisher) {
        AccessLoggerInterceptorConfiguration accessLoggerInterceptorConfiguration = new AccessLoggerInterceptorConfiguration();
        accessLoggerInterceptorConfiguration.addParser(new DefaultAccessLoggerParser());
        configurers.stream().forEach(configurer -> configurer.configure(accessLoggerInterceptorConfiguration));
        AccessLoggerInterceptor interceptor = new AccessLoggerInterceptor(eventPublisher, accessLoggerInterceptorConfiguration);
        AccessLoggerAdvisor advisor = new AccessLoggerAdvisor(interceptor);
        return advisor;
    }

    @Bean(name = "accessLoggerObjectMapper")
    public ObjectMapper accessLoggerObjectMapper() {
        ObjectMapper accessLoggerObjectMapper = new ObjectMapper();

        JacksonUtil.initWrapperObjectMapper(accessLoggerObjectMapper);
        configurers.stream().forEach(configurer -> configurer.configure(accessLoggerObjectMapper));
        return accessLoggerObjectMapper;
    }


    @Bean
    public DefaultAccessLoggerListener defaultAccessLoggerListener(ObjectMapper accessLoggerObjectMapper) {
        DefaultAccessLoggerListener defaultAccessLoggerListener = new DefaultAccessLoggerListener(accessLoggerObjectMapper);

        return defaultAccessLoggerListener;
    }


}
