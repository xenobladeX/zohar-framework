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
package com.xenoblade.zohar.framework.sample.agares.service.converter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xenoblade.zohar.framework.commons.utils.converter.DateConverter;
import com.xenoblade.zohar.framework.sample.agares.api.dto.UserDTO;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.SaveUserRequest;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.UpdateUserRequest;
import com.xenoblade.zohar.framework.sample.agares.service.entity.UserEntity;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * UserConverter
 * @author xenoblade
 * @since 1.0.0
 */
@Mapper(uses = {
        DateConverter.class }, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    /**
     * {@link UserEntity} 转 {@link UserDTO}
     * @param userEntity
     * @return
     */
    UserDTO userEntityToUserDTO(UserEntity userEntity);

    /**
     * {@link IPage<UserEntity>} 转 {@link IPage<UserDTO>}
     * @param userEntityPage
     * @return
     */
    Page<UserDTO> userEntityPageToUserDTOPage(Page<UserEntity> userEntityPage);

    /**
     * {@link SaveUserRequest} 转 {@link UserEntity}
     * @param saveUserRequest
     * @param userId
     * @return
     */
    UserEntity saveUserRequestToUserEntity(SaveUserRequest saveUserRequest, String userId);

    /**
     * {@link UpdateUserRequest} 转 {@link UserEntity}
     * @param updateUserRequest
     * @return
     */
    UserEntity updateUserRequestToUserEntity(UpdateUserRequest updateUserRequest);

}
