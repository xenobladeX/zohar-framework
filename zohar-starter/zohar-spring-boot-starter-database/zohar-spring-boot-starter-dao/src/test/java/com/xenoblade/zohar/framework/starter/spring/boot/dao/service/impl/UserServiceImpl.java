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
package com.xenoblade.zohar.framework.starter.spring.boot.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xenoblade.zohar.framework.starter.spring.boot.dao.entity.User;
import com.xenoblade.zohar.framework.starter.spring.boot.dao.mapper.UserMapper;
import com.xenoblade.zohar.framework.starter.spring.boot.dao.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserServiceImpl
 * @author xenoblade
 * @since 1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public void addUser(User user) {
        baseMapper.addUser(user.getName(), user.getAge());
    }

    @Override public Integer addUser2(User user) {
        return baseMapper.insert(user);
    }

    @Override
    public List selectUsersFromDs() {
        return baseMapper.selectUsers(1);
    }

    @Override
    public List selectUserFromDsGroup() {
        return baseMapper.selectUsers(1);
    }
}
