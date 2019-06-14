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
package com.xenoblade.zohar.framework.sample.agares.api.service;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * UserService
 * @author xenoblade
 * @since 1.0.0
 */
public class UserService {

    @Data
    @Accessors(chain = true)
    public static class GetUserPageRequest implements Serializable {

        private static final long serialVersionUID = -6985521050758761550L;

        /**
         * 页数
         */
        private Long current = 1L;

        /**
         * 每页的数量
         */
        private Long size = 10L;


        /**
         * 用户名
         */
        private String username;

        /**
         * 昵称
         */
        private String nickname;

    }


    /**
     * 保存用户的请求
     */
    @Data
    @Accessors(chain = true)
    public static class SaveUserRequest implements Serializable{

        private static final long serialVersionUID = -2123547171922733366L;

        /**
         * 用户名
         */
        @NotBlank(message = "用户名不能为空")
        private String username;

        /**
         * 密码
         */
        @NotBlank(message = "密码不能为空")
        private String password;

        /**
         * 昵称
         */
        private String nickname;

        /**
         * 手机号
         */
        private String mobileNumber;

        /**
         * 头像
         */
        private String avatar;

        /**
         * 邮箱地址
         */
        private String email;

    }

    /**
     * 保存用户的响应
     */
    @Data
    @Accessors(chain = true)
    public static class SaveUserResponse implements Serializable{

        private static final long serialVersionUID = 7737136784596936870L;

        /**
         * 用户 id
         */
        private String userId;
    }


    /**
     * 更新用户的请求
     */
    @Data
    @Accessors(chain = true)
    public static class UpdateUserRequest implements Serializable{

        private static final long serialVersionUID = -2123547171922733366L;

        /**
         * 用户 id
         */
        private String userId;

        /**
         * 密码
         */
        private String password;

        /**
         * 昵称
         */
        private String nickname;

        /**
         * 手机号
         */
        private String mobileNumber;

        /**
         * 头像
         */
        private String avatar;

        /**
         * 邮箱地址
         */
        private String email;

    }

}
