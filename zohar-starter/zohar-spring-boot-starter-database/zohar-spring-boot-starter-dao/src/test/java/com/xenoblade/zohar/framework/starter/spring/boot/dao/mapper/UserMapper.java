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
package com.xenoblade.zohar.framework.starter.spring.boot.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xenoblade.zohar.framework.starter.spring.boot.dao.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * UserMapper
 * @author xenoblade
 * @since 1.0.0
 */
public interface UserMapper extends BaseMapper<User> {

    @Insert("INSERT INTO user (name,age) values (#{name},#{age})")
    boolean addUser(@Param("name") String name, @Param("age") Integer age);

    @Select("SELECT * FROM user where age > #{age}") List<User> selectUsers(
            @Param("age") Integer age);
}
