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
package com.xenoblade.zohar.framework.sample.baal.service;

import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import com.xenoblade.zohar.framework.sample.baal.api.dto.UserDTO;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.GetUserParam;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.RemoveUserRequest;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.SaveUserRequest;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.UpdateUserRequest;
import com.xenoblade.zohar.framework.sample.baal.entity.UserEntity;

/**
 * IUserService
 * @author xenoblade
 * @since 1.0.0
 */
public interface IUserService {

    UserDTO getUser(GetUserParam getUserParam)throws ZoharException;

    UserDTO saveUser(SaveUserRequest saveUserRequest)throws ZoharException;

    void updateUser(UpdateUserRequest updateUserRequest)throws ZoharException;

    void removeUser(RemoveUserRequest removeUserRequest)throws ZoharException;

}
