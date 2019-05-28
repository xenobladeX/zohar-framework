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
package com.xenoblade.zohar.framework.sample.baal.api.service;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * UserService
 * @author xenoblade
 * @since 1.0.0
 */
public interface UserService {


    @Data
    @Accessors(chain = true)
    class GetUserParam implements Serializable{

        private static final long serialVersionUID = -1925505056679861889L;

        @NotBlank(message = "用户id不能为空")
        private String userId;

    }


    @Data
    @Accessors(chain = true)
    class SaveUserRequest implements Serializable{

        private static final long serialVersionUID = 1467601750653695215L;

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
         * 电话号码
         */
        private String phone;

        /**
         * 邮箱地址
         */
        @Email
        private String email;

    }

    @Data
    @Accessors(chain = true)
    class UpdateUserRequest implements Serializable{

        private static final long serialVersionUID = -4398634559280659065L;

        /**
         * 用户 id
         */
        @NotBlank(message = "用户id不能为空")
        private String userId;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 盐
         */
        private String salt;

        /**
         * 电话号码
         */
        private String phone;

        /**
         * 邮箱地址
         */
        @Email
        private String email;


    }


    @Data
    @Accessors(chain = true)
    class RemoveUserRequest implements Serializable{

        private static final long serialVersionUID = 342770468941455961L;

        private String userId;

    }


}
