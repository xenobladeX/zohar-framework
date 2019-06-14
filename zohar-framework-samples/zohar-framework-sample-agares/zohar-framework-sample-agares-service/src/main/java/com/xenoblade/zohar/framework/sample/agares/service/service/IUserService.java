package com.xenoblade.zohar.framework.sample.agares.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import com.xenoblade.zohar.framework.sample.agares.api.dto.UserDTO;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService;
import com.xenoblade.zohar.framework.sample.agares.api.service.UserService.SaveUserResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xenoblade
 * @since 2019-06-14
 */
public interface IUserService {

    /**
     * 通过用户id获取用户
     * @param userId
     * @return
     */
    UserDTO getUserById(String userId);


    IPage<UserDTO> getUserPage(UserService.GetUserPageRequest getUserPageRequest);

    /**
     * 保存用户
     * @param saveUserRequest
     * @return
     */
    SaveUserResponse saveUser(UserService.SaveUserRequest saveUserRequest) throws ZoharException;

    /**
     * 更新用户信息
     * @param updateUserRequest
     */
    void updateUser(UserService.UpdateUserRequest updateUserRequest);

}
