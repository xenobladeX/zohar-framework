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
package com.xenoblade.zohar.framework.tool;

import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.tool.converter.DateConverter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * DateConverterTest
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class DateConverterTest {

    Logger logger = LoggerFactory.getLogger(DateConverterTest.class);

    @Test
    public void testDateToLong() {
        String dateString = "2022-02-03 16:57:00";
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(dateString);
        } catch (ParseException ex) {
            logger.error("Parse date string {} failed; ", dateString, ex);
            throw new RuntimeException(StrUtil.format("Parse date string {} failed", dateString));
        }
        Long dateTimestamp = DateConverter.INSTANCE.DateToLong(date);
        Assert.assertEquals(Long.valueOf(1643878620000L), dateTimestamp);
    }

    @Test
    public void testLongToDate() {
        String dateString = "2022-02-03 16:57:00";
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(dateString);
        } catch (ParseException ex) {
            logger.error("Parse date string {} failed; ", dateString, ex);
            throw new RuntimeException(StrUtil.format("Parse date string {} failed", dateString));
        }
        Date calcDate = DateConverter.INSTANCE.LongToDate(Long.valueOf(1643878620000L));
        Assert.assertEquals(calcDate, date);
    }

    @Test
    public void testLongToLocalDateTime() {
        String dateTimeString = "2022-02-03 16:57:00";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, df);
        LocalDateTime calcDateTime = DateConverter.INSTANCE.LongToLocalDateTime(Long.valueOf(1643878620000L));
        Assert.assertEquals(calcDateTime, dateTime);
    }

    @Test
    public void testLocalDateTimeToLong() {
        String dateTimeString = "2022-02-03 16:57:00";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, df);
        Long dateTimestamp = DateConverter.INSTANCE.LocalDateTimeToLong(dateTime);
        Assert.assertEquals(Long.valueOf(1643878620000L), dateTimestamp);
    }

}