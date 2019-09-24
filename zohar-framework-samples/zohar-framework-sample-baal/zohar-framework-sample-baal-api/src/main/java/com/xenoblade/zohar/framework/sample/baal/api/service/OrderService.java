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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * OrderService
 * @author xenoblade
 * @since 1.0.0
 */
public interface OrderService {

    @Data
    @Accessors(chain = true)
    class GetOrderRequest implements Serializable{

        private static final long serialVersionUID = -8389522861842729639L;

        @NotNull(message = "orderId 不能为空")
        private Long orderId;

    }

    @Data
    @Accessors(chain = true)
    class SaveOrderRequest implements Serializable {

        private static final long serialVersionUID = -5437084329558879327L;

        @NotNull(message = "userId 不能为空")
        private Long userId;

        @NotEmpty(message = "用户名不能为空")
        private String userName;

    }

}
