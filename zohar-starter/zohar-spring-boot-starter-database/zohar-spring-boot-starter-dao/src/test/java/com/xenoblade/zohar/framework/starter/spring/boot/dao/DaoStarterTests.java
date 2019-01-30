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
package com.xenoblade.zohar.framework.starter.spring.boot.dao;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.xenoblade.zohar.framework.starter.spring.boot.dao.entity.User;
import com.xenoblade.zohar.framework.starter.spring.boot.dao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

/**
 * DaoStarterTests
 * @author xenoblade
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DaoStarterTests.TestConfig.class)
@Slf4j
public class DaoStarterTests {


    private Random random = new Random();

    @Autowired
    private UserService userService;

    @Autowired
    private DataSource dataSource;

        @Before
        public void beforeTest() {
            try {
                Connection connection = dataSource.getConnection();
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS  USER (\n" +
                        "  id BIGINT(20) NOT NULL AUTO_INCREMENT,\n" +
                        "  name VARCHAR(30) NULL DEFAULT NULL ,\n" +
                        "  age INT(11) NULL DEFAULT NULL ,\n" +
                        "  PRIMARY KEY (id)\n" +
                        ");");
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    @Test
    public void addUser() {
        User user = new User();
        user.setName("测试用户" + random.nextInt());
        user.setAge(random.nextInt(100));
        userService.addUser2(user);
    }

    @Test
    public void selectUsersFromDs() {
        userService.selectUsersFromDs();
    }

    @Test
    public void selectUserFromDsGroup() {
        userService.selectUserFromDsGroup();
    }

    @Test
    public void test() {
            String test = "dsjklg3uisdjfk32luy7";
            String md5 = DigestUtils.md5DigestAsHex(test.getBytes());
            log.info("md5: {}", md5);
    }



    @SpringBootApplication
    @MapperScan("com.xenoblade.zohar.framework.starter.spring.boot.dao.mapper")
    @Configuration
    public static class TestConfig {

    }
}
