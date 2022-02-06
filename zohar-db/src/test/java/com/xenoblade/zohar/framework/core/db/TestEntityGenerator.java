package com.xenoblade.zohar.framework.core.db;/*
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

import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;
import org.junit.jupiter.api.Test;

/**
 * com.xenoblade.zohar.framework.core.db.TestEntityGenerator
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class TestEntityGenerator {

    // 数据源 url
    // TODO h2
    static final String url = "jdbc:mysql://localhost:3306/fluent_mybatis?useUnicode=true&characterEncoding=utf8";
    // 数据库用户名
    static final String username = "root";
    // 数据库密码
    static final String password = "123456";


    @Test
    public void generate() throws Exception {
        // 引用配置类，build方法允许有多个配置类
        FileGenerator.build(Empty.class);
    }

    @Tables(
            // 设置数据库连接信息
            url = url, username = username, password = password,
            // 设置entity类生成src目录, 相对于 user.dir
            srcDir = "src/test/java",
            // 设置entity类的package值
            basePack = "com.xenoblade.zohar.framework.core.db",
            // 设置dao接口和实现的src目录, 相对于 user.dir
            daoDir = "src/test/java",
            // 设置哪些表要生成Entity文件
            tables = {@Table(value = {"hello_world"})}
    )
    static class Empty { //类名随便取, 只是配置定义的一个载体
    }

}