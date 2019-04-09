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
package com.xenoblade.zohar.framework.web.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.commons.log.core.config.AccessLoggerConfigurer;
import com.xenoblade.zohar.framework.commons.utils.jackson.JacksonUtil;
import com.xenoblade.zohar.framework.commons.web.version.ApiRequestMappingHandlerMapping;
import com.xenoblade.zohar.framework.web.starter.log.AccessLoggerHttpConfigurer;
import com.xenoblade.zohar.framework.web.starter.version.VersionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * WebAutoConfiguration
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties({VersionProperties.class})
public class WebAutoConfiguration implements WebMvcConfigurer, WebMvcRegistrations {

    @Autowired
    private VersionProperties versionProperties;

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


    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        if (versionProperties.isEnabled()) {
            return new ApiRequestMappingHandlerMapping(versionProperties.getMinimumVersion(), versionProperties.isParsePackageVersion());
        } else {
            return null;
        }
    }

    /**
     * 引入RequestContextListener以支持Bean的另外3个作用域: request,session和globalSession
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach(e -> {
            if (e instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) e;
                // Modify objectMapper
                converter.getObjectMapper();
            }
        });
    }

    /**
     * 自定义 http ObjectMapper
     * @param builder
     * @return
     */
    @Bean
    public ObjectMapper httpObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        JacksonUtil.initWrapperObjectMapper(objectMapper);

        return objectMapper;
    }

    @Bean
//    @ConditionalOnMissingBean(value = MappingJackson2HttpMessageConverter.class, ignoredType = {
//            "org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter",
//            "org.springframework.data.rest.webmvc.alps.AlpsJsonHttpMessageConverter" })
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
            @Qualifier("httpObjectMapper") ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }


    /**
     * 自定义全局异常处理
     * @return
     */
    @Bean
    public RestControllerExceptionHandler restControllerExceptionHandler() {
        return new RestControllerExceptionHandler();
    }

    /**
     * custom AccessLoggerConfigurer
     */
    @Bean
    @ConditionalOnProperty(prefix = "zohar.log.access.context.http", name = "enable", havingValue = "true", matchIfMissing = true)
    public AccessLoggerConfigurer accessLoggerHttpConfigurer() {
        return new AccessLoggerHttpConfigurer();
    }

}
