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
package com.xenoblade.zohar.framework.sample.baal.controller;

import com.xenoblade.zohar.framework.commons.log.api.annotation.AccessLogger;
import com.xenoblade.zohar.framework.commons.web.msg.ResponseMessage;
import com.xenoblade.zohar.framework.sample.baal.api.dto.UserDTO;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.GetUserParam;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.RemoveUserRequest;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.SaveUserRequest;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.UpdateUserRequest;
import com.xenoblade.zohar.framework.sample.baal.service.IUserService;
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

import javax.validation.constraints.NotBlank;

/**
 * UserController
 * @author xenoblade
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@AccessLogger
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/{id}")
    public ResponseMessage<UserDTO> getUser(@NotBlank(message = "用户 id 不能为空") @PathVariable String id) {
        GetUserParam getUserParam = new GetUserParam();
        return ResponseMessage.ok(userService.getUser(getUserParam.setUserId(id)));
    }

    @PostMapping
    public ResponseMessage saveUser(@Validated @RequestBody SaveUserRequest saveUserRequest) {
        UserDTO userDTO = userService.saveUser(saveUserRequest);
        return ResponseMessage.ok(userDTO.getUserId());
    }

    @PutMapping("/{id}")
    public ResponseMessage updateUser(@NotBlank(message = "用户id不能为空") @PathVariable String id,
                                          @Validated @RequestBody UpdateUserRequest updateUserRequest) {
        updateUserRequest.setUserId(id);
        userService.updateUser(updateUserRequest);
        return ResponseMessage.ok();
    }

    @DeleteMapping("/{id}")
    public ResponseMessage removeUser(@NotBlank(message = "用户 id 不能为空") @PathVariable String id) {

        RemoveUserRequest removeUserRequest = new RemoveUserRequest();
        userService.removeUser(removeUserRequest.setUserId(id));
        return ResponseMessage.ok();
    }

}
