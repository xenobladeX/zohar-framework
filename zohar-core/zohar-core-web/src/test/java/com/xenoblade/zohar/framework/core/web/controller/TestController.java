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
package com.xenoblade.zohar.framework.core.web.controller;

import com.xenoblade.zohar.framework.core.api.dto.Response;
import com.xenoblade.zohar.framework.core.web.bean.TestBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * @author xenoblade
 * @since 0.0.1
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/getString")
    public String getString() {
        return "Hello world";
    }


    @GetMapping("/getBean")
    public TestBean getTestBean() {
        return new TestBean();
    }

    @GetMapping("/getResponse")
    public Response getResponse() {
        return Response.ok();
    }



}