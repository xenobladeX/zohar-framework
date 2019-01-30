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
package com.xenoblade.zohar.framework.starter.spring.boot.web;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import com.xenoblade.zohar.framework.commons.spring.log.AccessLoggerParser;
import com.xenoblade.zohar.framework.commons.web.log.HttpAccessLoggerParser;
import io.undertow.Undertow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * RestAutoConfiguration
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
public class RestAutoConfiguration implements WebMvcConfigurer {

    /**
     * Undertow服务器配置
     * @return
     */
//    @Bean
//    @ConditionalOnClass(Undertow.class)
//    public UndertowServerFactoryCustomizer undertowServerFactoryCustomizer() {
//        return new UndertowServerFactoryCustomizer();
//    }


    /**
     * 引入RequestContextListener以支持Bean的另外3个作用域: request,session和globalSession
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    /**
     * 设置json格式转换规则
     * @param converters
     */
    @Override public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach(e -> {
            if (e instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) e;
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
                simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
                converter.getObjectMapper().registerModule(simpleModule);

                // Jackson protobuf format
                converter.getObjectMapper().registerModule(new ProtobufModule());

                // Ignore NULL fields
                converter.getObjectMapper().setSerializationInclusion(Include.NON_NULL);
            }
        });
    }

    @Override public void addInterceptors(InterceptorRegistry registry) {
        // filter swagger
//        registry.addInterceptor(new GlobalInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/error",
//                        "/swagger-resources",
//                        "/swagger-resources/configuration/security",
//                        "/swagger-resources/configuration/ui",
//                        "/v2/api-docs",
//                        "/swagger-ui.html/**",
//                        "/webjars/**"
//                );
    }

    /**
     * logger parser
     */
    @Bean
    public AccessLoggerParser httpAccessLoggerParser() {
        return new HttpAccessLoggerParser();
    }

    @Bean
    public RestControllerExceptionTranslator restControllerExceptionTranslator() {
        return new RestControllerExceptionTranslator();
    }

}
