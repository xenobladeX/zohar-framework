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
package com.xenoblade.zohar.framework.tool.converter;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.time.*;
import java.util.Date;

/**
 * DateConverter
 * @author xenoblade
 * @since 1.0.0
 */
@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DateConverter {

    ZoneOffset defaultZoneOffSet = OffsetDateTime.now(ZoneId.systemDefault()).getOffset();

    DateConverter INSTANCE = Mappers.getMapper(DateConverter.class);

    default Long DateToLong(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    default Long LocalDateTimeToLong(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.toInstant(defaultZoneOffSet).toEpochMilli();
    }

    default Date LongToDate(Long date) {
        if (date == null) {
            return null;
        }
        return new Date(date);
    }

    default LocalDateTime LongToLocalDateTime(Long date) {
        if (date == null) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(date);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

}
