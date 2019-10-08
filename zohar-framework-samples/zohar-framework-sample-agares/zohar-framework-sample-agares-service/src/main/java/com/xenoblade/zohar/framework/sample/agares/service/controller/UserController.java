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
package com.xenoblade.zohar.framework.sample.agares.service.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xenoblade.zohar.framework.commons.web.msg.ResponseMessage;
import com.xenoblade.zohar.framework.sample.agares.api.dto.UserDTO;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.GetUserPageRequest;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.SaveUserRequest;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.SaveUserResponse;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.UpdateUserRequest;
import com.xenoblade.zohar.framework.sample.agares.service.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author xenoblade
 * @since 2019-06-14
 */
@RestController
@RequestMapping("/user")
@Validated
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/{userId}")
    public ResponseMessage<UserDTO> getUserById(@NotNull(message = "用户 id 不能为空")
                                                @PathVariable String userId, HttpServletRequest httpRequest) {

        UserDTO userDTO = userService.getUserById(userId);
        return ResponseMessage.ok(userDTO);
    }

//    @GetMapping("/list")
//    public ResponseMessage<List<UserDTO>> getUserList() {
//
//        return ResponseMessage.ok();
//    }

    @GetMapping("/page")
    public ResponseMessage<IPage<UserDTO>> getUserPage(@Validated GetUserPageRequest getUserPageRequest) {

        return ResponseMessage.ok(userService.getUserPage(getUserPageRequest));
    }


    @PostMapping
    public ResponseMessage<SaveUserResponse> saveUser(@Validated @RequestBody SaveUserRequest saveUserRequest) {
        SaveUserResponse saveUserResponse = userService.saveUser(saveUserRequest);
        return ResponseMessage.ok(saveUserResponse);
    }

    @PutMapping("/{userId}")
    public ResponseMessage updateUser(@NotNull(message = "用户 id 不能为空")
                                          @PathVariable String userId, @Validated @RequestBody
                                      UpdateUserRequest updateUserRequest) {
        updateUserRequest.setUserId(userId);
        userService.updateUser(updateUserRequest);
        return ResponseMessage.ok();
    }


    @DeleteMapping("/{userId}")
    public ResponseMessage removeUser(@NotNull(message = "用户 id 不能为空")
                          @PathVariable String userId) {


        return ResponseMessage.ok();
    }

}
