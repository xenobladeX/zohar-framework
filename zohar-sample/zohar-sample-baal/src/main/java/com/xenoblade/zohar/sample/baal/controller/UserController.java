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
package com.xenoblade.zohar.sample.baal.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xenoblade.zohar.sample.baal.entity.UserEntity;
import com.xenoblade.zohar.sample.baal.service.UserService;
import com.xenoblade.zohar.framework.commons.spring.log.api.AccessLogger;
import com.xenoblade.zohar.framework.commons.web.message.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * UserController
 * @author xenoblade
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
@AccessLogger("用户管理")
@Validated
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseMessage<List<UserEntity>> getUserList() {
        return ResponseMessage.ok(userService.list());
    }

    @PostMapping
    public ResponseMessage<Long> insertUser(@RequestBody UserEntity userEntity) {
        boolean ret = userService.save(userEntity);
        return ResponseMessage.ok(userEntity.getId());
    }

    @GetMapping("/page")
    public ResponseMessage<IPage<UserEntity>> getUserPage(Page<UserEntity> page) {
        return ResponseMessage.ok(userService.page(page));
    }

    @GetMapping("/{id:.+}")
    public ResponseMessage<UserEntity> getUser(@PathVariable @Min(0) Long id) {
        return ResponseMessage.ok(userService.getById(id));
    }

    @DeleteMapping("/{id:.+}")
    public ResponseMessage<Integer> deleteByPrimaryKey(@PathVariable @Min(0) Long id) {
        return ResponseMessage.ok(userService.deleteByPK(id) ? 1 : 0);
    }

}
