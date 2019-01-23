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
package com.xenoblade.zohar.sample.baal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.protobuf.ServiceException;
import com.xenoblade.zohar.framework.commons.spring.log.api.AccessLogger;
import com.xenoblade.zohar.framework.commons.web.message.ResponseMessage;
import com.xenoblade.zohar.sample.agares.api.Agares.AgaresService;
import com.xenoblade.zohar.sample.agares.api.Agares.HelloAgaresRequest;
import com.xenoblade.zohar.sample.agares.api.Agares.HelloAgaresResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BaalController
 * @author xenoblade
 * @since 1.0.0
 */
@RestController
@RequestMapping("/agares")
@AccessLogger("RPC 通信")
@Validated
@Slf4j
public class BaalController {

    @Reference(url = "dubbo://localhost:12345")
    public AgaresService.BlockingInterface agaresService;


    @PostMapping("/hello")
    @SneakyThrows(ServiceException.class)
    public ResponseMessage<HelloAgaresResponse> helloAgares() {
        HelloAgaresResponse response = agaresService.helloAgares(null, HelloAgaresRequest.newBuilder().setRequest("Hello, Agares").build());
        return ResponseMessage.ok(response);
    }

}
