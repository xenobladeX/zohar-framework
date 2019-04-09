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
package com.xenoblade.zohar.framework.sample.baal.controller;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.commons.api.enums.ZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.exception.NotFoundException;
import com.xenoblade.zohar.framework.commons.spring.ApplicationContextHolder;
import com.xenoblade.zohar.framework.commons.web.msg.ResponseMessage;
import com.xenoblade.zohar.framework.commons.log.api.annotation.AccessLogger;
import com.xenoblade.zohar.framework.sample.baal.api.BaalService;
import com.xenoblade.zohar.framework.sample.baal.service.IBaalService;
import io.undertow.server.handlers.proxy.mod_cluster.Balancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * BaalController
 * @author xenoblade
 * @since 1.0.0
 */
@RestController
@RequestMapping("/baal")
@Validated
@Slf4j
@AccessLogger
public class BaalController implements BaalService{

    @Autowired
    private IBaalService baalService;

    @PostMapping("/hello")
    public ResponseMessage<HelloBaalResponse> testRest(@RequestBody HelloBaalRequest request) {
        HelloBaalResponse response = baalService.testRest(request);
        return ResponseMessage.ok(response);
    }

    @GetMapping("/exception/{errorCode}")
    public ResponseMessage testException(@PathVariable Integer errorCode) {
        baalService.testException(errorCode);

        return ResponseMessage.ok();
    }

    @PostMapping("/validate/{id}")
    public ResponseMessage testVallidate(@Min(value = 1, message = "id 必须大于0") @PathVariable Integer id, @Validated @RequestBody TestVallidateRequest request) {

        baalService.testVallidate(id, request);

        return ResponseMessage.ok();
    }

    @PostMapping("/exclude")
    public ResponseMessage<TestExcludeBody> testExclude(@RequestParam(value = "exclude", required = false) List<String> excludeFieldList, @RequestBody TestExcludeBody excludeBody) {
        TestExcludeBody newBody = baalService.testExclude(excludeFieldList, excludeBody);

        return ResponseMessage.ok(newBody);
    }
}
