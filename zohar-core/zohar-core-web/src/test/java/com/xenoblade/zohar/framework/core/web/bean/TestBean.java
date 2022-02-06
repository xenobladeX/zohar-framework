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
package com.xenoblade.zohar.framework.core.web.bean;

import lombok.Data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TestBean
 *
 * @author xenoblade
 * @since 0.0.1
 */
@Data
public class TestBean implements Serializable {

    private static final long serialVersionUID = -8364467902564540495L;

    private String aNull;

    private LocalDateTime aLocalDateTime;

    private Long aLong = 3257342073940842385L;

    public TestBean() {
        String dateString = "2022-02-03 16:57:00";
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.aLocalDateTime = LocalDateTime.parse(dateString, df);
    }

}