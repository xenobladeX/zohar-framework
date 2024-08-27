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
package com.xenoblade.zohar.framework.openapi.controller;

import com.xenoblade.zohar.framework.openapi.dto.SayHiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xenoblade
 * @ClassName IndexController
 * @Description
 * @Date 2022/11/30
 * @since 0.0.1
 */
@Tag(name = "首页模块")
@RestController
public class IndexController {

    @Parameter(name = "name",description = "姓名",required = true)
    @Operation(summary = "向客人问好")
    @GetMapping("/sayHi")
    public SayHiResponse sayHi(@RequestParam(value = "name")String name){
        return new SayHiResponse().setMessage("Hi " + name);
    }
}