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
package com.xenoblade.zohar.framework.tool.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.xenoblade.zohar.framework.core.api.dto.BasicErrorCode;
import com.xenoblade.zohar.framework.core.api.exception.SysException;
import com.xenoblade.zohar.framework.core.extension.ExtensionFinderFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.util.Locale.CHINA;

/**
 * JacksonUtil
 *
 * @author xenoblade
 * @since 0.0.1
 */
public final class JacksonUtil {

    private static ObjectMapper OBJECT_MAPPER;

    public static ObjectMapper getObjectMapper() {
        if (OBJECT_MAPPER == null) {
            OBJECT_MAPPER = initObjectMapper();
        }
        return OBJECT_MAPPER;
    }

    private static ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = JsonMapper.builder()
                // 忽略transient
                .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
                .build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 不显示为null的字段
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 忽略不能转义的字符
        objectMapper.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
        // 过滤对象的null属性.
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置时间格式
        SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", CHINA);
        objectMapper.setDateFormat(myDateFormat);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", CHINA)));
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", CHINA)));
        objectMapper.registerModule(javaTimeModule);
        // 大数字转义
        SimpleModule numberModule = new NumberModule();
        objectMapper.registerModule(numberModule);

        objectMapper.findAndRegisterModules();

        // custom ObjectMapper
        ExtensionFinderFactory.getExtensionFinder().find(JacksonUtilConfigurer.class).forEach(jacksonUtilConfigurerExtensionWrapper -> {
            jacksonUtilConfigurerExtensionWrapper.getExtension().customObjectMapper(objectMapper);
        });

        return objectMapper;
    }

    /**
     * 转换Json
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        if (isCharSequence(object)) {
            return (String) object;
        }
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SysException(BasicErrorCode.JASON_PROCESS_ERROR, e);
        }
    }

    /**
     * <p>
     * 是否为CharSequence类型
     * </p>
     *
     * @param object
     * @return
     */
    public static Boolean isCharSequence(Object object) {
        return !Objects.isNull(object) &&
                (object.getClass() != null &&
                        CharSequence.class.isAssignableFrom(object.getClass()));
    }

    /**
     * Json转换为对象 转换失败返回null
     *
     * @param jsonStr
     * @param valueType
     * @return
     */
    public static <T> T parse(String jsonStr, Class<T> valueType) {

        try {
            return getObjectMapper().readValue(jsonStr, valueType);
        } catch (Exception e) {
            throw new SysException(BasicErrorCode.JASON_PROCESS_ERROR, e);
        }
    }


}