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
package com.xenoblade.zohar.framework.sample.baal.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheConfig;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CacheEvict;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.CachePut;
import com.xenoblade.zohar.framework.cache.aspectj.annotation.Cacheable;
import com.xenoblade.zohar.framework.commons.api.exception.NotFoundException;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import com.xenoblade.zohar.framework.sample.baal.api.dto.UserDTO;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.GetUserParam;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.RemoveUserRequest;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.SaveUserRequest;
import com.xenoblade.zohar.framework.sample.baal.api.service.UserService.UpdateUserRequest;
import com.xenoblade.zohar.framework.sample.baal.converter.UserConverter;
import com.xenoblade.zohar.framework.sample.baal.entity.UserEntity;
import com.xenoblade.zohar.framework.sample.baal.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * UserServiceImpl
 * @author xenoblade
 * @since 1.0.0
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "baal-user")
public class UserServiceImpl implements IUserService{

    private static Map<String, UserEntity> localUsers = Maps.newConcurrentMap();


    @Cacheable(key = "#getUserParam.userId")
    @Override
    public UserDTO getUser(GetUserParam getUserParam) throws ZoharException{
        log.info("Get user: {}", getUserParam);
        UserEntity userEntity = localUsers.entrySet().stream().map(Map.Entry::getValue)
                .filter(user -> getUserParam.getUserId().equals(user.getUserId()))
                .findAny()
                .orElseThrow(NotFoundException::new);

        return UserConverter.INSTANCE.userEntityToUserDTO(userEntity);
    }

    @CachePut(key = "#result.userId")
    @Override
    public UserDTO saveUser(SaveUserRequest saveUserRequest) throws ZoharException{
        log.info("Save user: {}", saveUserRequest);
        UserEntity userEntity = UserConverter.INSTANCE.saveUserRequestToUserEntity(saveUserRequest);
        userEntity.setUserId(IdUtil.simpleUUID());
        userEntity.setCreateTime(LocalDateTime.now());
        userEntity.setUpdateTime(LocalDateTime.now());
        UserEntity maxUserEntity = localUsers.entrySet().stream()
                .map(Map.Entry::getValue)
                .max(Comparator.comparing(UserEntity::getId)).orElse(null);
        if (maxUserEntity == null) {
            userEntity.setId(0L);
        } else {
            userEntity.setId(maxUserEntity.getId() + 1L);
        }

        localUsers.put(userEntity.getUserId(), userEntity);
        return UserConverter.INSTANCE.userEntityToUserDTO(userEntity);
    }

    @CachePut(key = "#updateUserRequest.userId")
    @Override
    public UserDTO updateUser(UpdateUserRequest updateUserRequest) throws ZoharException{
        log.info("Update user: {}", updateUserRequest);
        UserEntity userEntity = localUsers.entrySet().stream().map(Map.Entry::getValue)
                .filter(user -> updateUserRequest.getUserId().equals(user.getUserId()))
                .findAny().orElseThrow(NotFoundException::new);
        if (StrUtil.isNotBlank(updateUserRequest.getUsername())) {
            userEntity.setUsername(updateUserRequest.getUsername());
        }
        if (StrUtil.isNotBlank(updateUserRequest.getPassword())) {
            userEntity.setPassword(updateUserRequest.getPassword());
        }
        if (StrUtil.isNotBlank(updateUserRequest.getSalt())) {
            userEntity.setSalt(updateUserRequest.getSalt());
        }
        if (StrUtil.isNotBlank(updateUserRequest.getEmail())) {
            userEntity.setEmail(updateUserRequest.getEmail());
        }
        if (StrUtil.isNotBlank(updateUserRequest.getPhone())) {
            userEntity.setPhone(updateUserRequest.getPhone());
        }

        localUsers.put(userEntity.getUserId(), userEntity);
        return UserConverter.INSTANCE.userEntityToUserDTO(userEntity);
    }

    @CacheEvict(key = "#removeUserRequest.userId")
    @Override
    public void removeUser(RemoveUserRequest removeUserRequest) throws ZoharException{
        log.info("Remove user: {}", removeUserRequest);
        Optional.ofNullable(localUsers.remove(removeUserRequest.getUserId()))
                .orElseThrow(NotFoundException::new);
    }
}
