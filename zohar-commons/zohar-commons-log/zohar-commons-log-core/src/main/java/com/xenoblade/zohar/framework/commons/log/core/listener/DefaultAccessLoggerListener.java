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
package com.xenoblade.zohar.framework.commons.log.core.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.commons.log.api.AccessLoggerInfo;
import com.xenoblade.zohar.framework.commons.log.api.event.AccessLoggerAfterEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;


/**
 * DefaultAccessLoggerListener
 * @author xenoblade
 * @since 1.0.0
 */
@AllArgsConstructor
public class DefaultAccessLoggerListener {

    private static Logger logger = LoggerFactory.getLogger("access-logger");

    private ObjectMapper accessLoggerObjectMapper;

    @EventListener public void onLogger(AccessLoggerAfterEvent event) {
        AccessLoggerInfo info = event.getLogger();

        try {
            if (info.getException() != null) {
                logger.error(accessLoggerObjectMapper.writeValueAsString(info));
            } else if (logger.isInfoEnabled()) {
                logger.info(accessLoggerObjectMapper.writeValueAsString(info));
            }
        } catch (JsonProcessingException ex) {
            logger.error("Json Processing AccessLoggerInfo failed: ", ex);
        }
    }

}
