package com.xenoblade.zohar.framework.sample.agares.service.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xenoblade.zohar.framework.commons.api.enums.ZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.exception.NotFoundException;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import com.xenoblade.zohar.framework.sample.agares.api.dto.UserDTO;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.GetUserPageRequest;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.SaveUserRequest;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.SaveUserResponse;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.UpdateUserRequest;
import com.xenoblade.zohar.framework.sample.agares.service.converter.UserConverter;
import com.xenoblade.zohar.framework.sample.agares.service.entity.UserEntity;
import com.xenoblade.zohar.framework.sample.agares.service.manager.UserManager;
import com.xenoblade.zohar.framework.sample.agares.service.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xenoblade
 * @since 2019-06-14
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserManager userManager;

    @Override public UserDTO getUserById(String userId) {
        UserEntity userEntity = userManager.getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getUserId, userId));
        if (userEntity == null) {
            throw new NotFoundException("用户不存在");
        }
        return UserConverter.INSTANCE.userEntityToUserDTO(userEntity);
    }

    @Override public IPage<UserDTO> getUserPage(GetUserPageRequest getUserPageRequest) {
        IPage<UserEntity> userEntityPage = userManager
                .page(new Page<UserEntity>(getUserPageRequest.getCurrent(),
                        getUserPageRequest.getSize()), Wrappers.<UserEntity>lambdaQuery()
                        .like( getUserPageRequest.getUsername() != null, UserEntity::getUsername, getUserPageRequest.getUsername())
                        .like(getUserPageRequest.getNickname() != null, UserEntity::getNickname, getUserPageRequest.getNickname()));


        return UserConverter.INSTANCE.userEntityPageToUserDTOPage((Page)userEntityPage);
    }

    @Override
    public SaveUserResponse saveUser(SaveUserRequest saveUserRequest) throws ZoharException{

        if (userManager.count(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getUsername, saveUserRequest.getUsername())) > 0) {
            throw new ZoharException(ZoharErrorCode.DATA_DUPLICATE);
        }

        UserEntity userEntity = UserConverter.INSTANCE.saveUserRequestToUserEntity(saveUserRequest,
                IdUtil.simpleUUID());
        if (!StrUtil.isNotBlank(userEntity.getNickname())) {
            userEntity.setNickname(userEntity.getUsername());
        }
        userManager.save(userEntity);
        return new SaveUserResponse().setUserId(userEntity.getUserId());
    }

    @Override public void updateUser(UpdateUserRequest updateUserRequest) {

        UserEntity userEntity = UserConverter.INSTANCE.updateUserRequestToUserEntity(updateUserRequest);

        if(!userManager.update(userEntity, Wrappers.<UserEntity>lambdaUpdate()
                .eq(UserEntity::getUserId, userEntity.getUserId()))) {
            throw new ZoharException(ZoharErrorCode.DATA_UPDATE_ERROR);
        }
    }

}
