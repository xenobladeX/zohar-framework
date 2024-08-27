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
package com.xenoblade.zohar.framework.openapi.dto;

import org.zohar.framework.core.api.dto.DTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xenoblade
 * @ClassName SayHiResponse
 * @Description
 * @Date 2022/11/30
 * @since 0.0.1
 */
@Accessors(chain = true)
@Data
public class SayHiResponse extends DTO {

    private static final long serialVersionUID = -481939279861145201L;

    private String message;
}