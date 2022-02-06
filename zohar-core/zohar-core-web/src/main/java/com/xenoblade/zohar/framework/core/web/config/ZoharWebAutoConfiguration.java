/*
 * Copyright [2022] [xenoblade]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.core.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.core.web.advice.BasicRestExceptionHandler;
import com.xenoblade.zohar.framework.core.web.advice.ZoharResponseAdvice;
import com.xenoblade.zohar.framework.core.web.advice.ZoharRestExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * WebMessageConfiguration
 *
 * @author xenoblade
 * @since 0.0.1
 */
@Configuration
public class ZoharWebAutoConfiguration implements WebMvcConfigurer {

    private ObjectMapper objectMapper;

    public ZoharWebAutoConfiguration(@Qualifier("webObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(x -> x instanceof StringHttpMessageConverter || x instanceof MappingJackson2HttpMessageConverter);
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new ResourceRegionHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
    }

//    @Bean
//    public BasicRestExceptionHandler basicRestExceptionHandler() {
//        return new BasicRestExceptionHandler();
//    }
//
//    @Bean
//    public ZoharResponseAdvice zoharResponseAdvice() {
//        return new ZoharResponseAdvice();
//    }
//
//    @Bean
//    public ZoharRestExceptionHandler zoharRestExceptionHandler() {
//        return new ZoharRestExceptionHandler();
//    }

}