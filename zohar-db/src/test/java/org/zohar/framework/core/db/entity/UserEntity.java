/*
 * Copyright [2022] [xenoblade]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zohar.framework.core.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.zohar.framework.core.api.dto.DTO;
import lombok.Data;

/**
 * @author xenoblade
 * @ClassName UserEntity
 * @Description
 * @Date 2022/11/30
 * @since 0.0.1
 */
@TableName("user")
@Data
public class UserEntity extends DTO {

    private static final long serialVersionUID = -8571966561214792824L;

    @TableId
    private Long id;

    private String name;

    private Integer age;

    private String email;

}