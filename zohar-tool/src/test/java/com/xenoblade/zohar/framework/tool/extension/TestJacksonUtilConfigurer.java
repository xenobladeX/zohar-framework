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
package com.xenoblade.zohar.framework.tool.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.core.extension.Extension;
import com.xenoblade.zohar.framework.tool.jackson.JacksonUtilConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TestJacksonUtilConfigurer
 *
 * @author xenoblade
 * @since 0.0.1
 */
@Extension
public class TestJacksonUtilConfigurer implements JacksonUtilConfigurer {

    private Logger logger = LoggerFactory.getLogger(TestJacksonUtilConfigurer.class);

    public static Boolean isExecuted = false;

    @Override
    public void customObjectMapper(ObjectMapper objectMapper) {
        logger.info("custom objectMapper {}", objectMapper);
        isExecuted = true;
    }

    @Override
    public int order() {
        return 1;
    }
}