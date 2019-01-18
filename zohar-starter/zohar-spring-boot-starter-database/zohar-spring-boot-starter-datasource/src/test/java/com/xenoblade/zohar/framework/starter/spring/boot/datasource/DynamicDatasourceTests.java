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
package com.xenoblade.zohar.framework.starter.spring.boot.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.xenoblade.zohar.framework.starter.spring.boot.datasource.entity.User;
import com.xenoblade.zohar.framework.starter.spring.boot.datasource.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Random;

/**
 * DynamicDatasourceTests
 * @author xenoblade
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DynamicDatasourceTests.TestConfig.class)
@Slf4j
public class DynamicDatasourceTests extends AbstractJUnit4SpringContextTests {

    private Random random = new Random();

    @Autowired
    private UserService userService;

    @Autowired
    private DataSource dataSource;


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



    @SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
    @MapperScan("com.xenoblade.zohar.framework.starter.spring.boot.datasource.mapper")
    @Configuration
    public static class TestConfig {

    }


}
