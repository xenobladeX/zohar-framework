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
package com.xenoblade.zohar.framework.tool.bean;

import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.tool.PinyinTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * TestDateBean
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class TestDateBean implements Serializable {
    private static final long serialVersionUID = 2208298740415793742L;

    private Logger logger = LoggerFactory.getLogger(PinyinTest.class);

    private LocalDateTime localDateTime;

    private Date date;

    public TestDateBean() {
        String dateString = "2022-02-03 16:57:00";
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.date = fmt.parse(dateString);
        } catch (ParseException ex) {
            logger.error("Parse date string {} failed; ", dateString, ex);
            throw new RuntimeException(StrUtil.format("Parse date string {} failed", dateString));
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.localDateTime = LocalDateTime.parse(dateString, df);
    }

    public Date getDate() {
        return date;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}