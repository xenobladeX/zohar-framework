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
package com.xenoblade.zohar.framework.core.db;

import com.xenoblade.zohar.framework.core.db.entity.HelloWorldEntity;
import com.xenoblade.zohar.framework.core.db.mapper.HelloWorldMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * FluentMybatisTest
 *
 * @author xenoblade
 * @since 0.0.1
 */
@SpringBootTest(classes = TestApplication.class)
@Slf4j
public class FluentMybatisTest {

    @Autowired
    private HelloWorldMapper helloWorldMapper;

    @Test
    public void test() {
        // 全表清空
        helloWorldMapper.delete(helloWorldMapper.query().where.apply("1=1").isTrue().end());
        // 插入记录，未设置gmtCreated, gmtModified, isDeleted3个字段
        helloWorldMapper.insert(new HelloWorldEntity()
                .setSayHello("hello")
                .setYourName("fluent mybatis")
        );
        // 查询并在控制台输出记录
        HelloWorldEntity entity = helloWorldMapper.findOne(helloWorldMapper.query()
                .where.sayHello().eq("hello")
                .and.yourName().eq("fluent mybatis").end()
        );
        log.info("get entity: {}", entity);
    }

}