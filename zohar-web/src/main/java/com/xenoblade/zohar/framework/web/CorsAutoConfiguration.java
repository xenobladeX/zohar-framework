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
package com.xenoblade.zohar.framework.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.Optional;

/**
 * CorsAutoConfiguration
 * @author xenoblade
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "zohar.web.cors", name = "enable", havingValue = "true")
@EnableConfigurationProperties(CorsProperties.class)
public class CorsAutoConfiguration {

    /**
     * 默认匹配全部
     */
    private static final String CORS_PATH_ALL = "/**";

    @Bean
    public CorsFilter corsFilter(CorsProperties corsProperties) {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();

        Optional.ofNullable(corsProperties.getConfigs())
                .orElse(Collections.singletonList(Collections.singletonMap(CORS_PATH_ALL,
                        new CorsProperties.CorsConfiguration().applyPermitDefaultValues())))
                .forEach((map) ->
                        map.forEach((path, config) ->
                                corsConfigurationSource.registerCorsConfiguration(path, buildConfiguration(config))
                        )
                );

        return new CorsFilter(corsConfigurationSource);
    }

    private CorsConfiguration buildConfiguration(CorsProperties.CorsConfiguration config) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(config.getAllowedHeaders());
        corsConfiguration.setAllowedMethods(config.getAllowedMethods());
        corsConfiguration.setAllowedOrigins(config.getAllowedOrigins());
        corsConfiguration.setAllowCredentials(config.getAllowCredentials());
        corsConfiguration.setExposedHeaders(config.getExposedHeaders());
        corsConfiguration.setMaxAge(config.getMaxAge());

        return corsConfiguration;
    }
}
