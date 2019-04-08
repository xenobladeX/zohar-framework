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
import com.xenoblade.zohar.framework.commons.api.enums.ZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.exception.NotFoundException;
import com.xenoblade.zohar.framework.commons.web.msg.ResponseMessage;
import com.xenoblade.zohar.framework.sample.baal.api.BaalService;
import com.xenoblade.zohar.framework.sample.baal.service.IBaalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BaalController implements BaalService{

    @Autowired
    private IBaalService baalService;

    @PostMapping("/hello")
    @Override
    public ResponseMessage<HelloBaalResponse> helloBaal(@RequestBody HelloBaalRequest request) {
        log.info("Post to helloBaal with request: {}", request);
        HelloBaalResponse response = new HelloBaalResponse();
        response.setResponse("Hello, this is baal v2");
        return ResponseMessage.ok(response);
    }

    @GetMapping("/exception/{errorCode}")
    @Override
    public ResponseMessage testException(@PathVariable Integer errorCode) {
        ZoharErrorCode zoharErrorCode = ZoharErrorCode.CodeOf(errorCode);
        throw new NotFoundException(zoharErrorCode);
    }

    @PostMapping("/validate/{id}")
    @Override
    public ResponseMessage testVallidate(@Min(value = 1, message = "id 必须大于0") @PathVariable Integer id, @Validated @RequestBody TestVallidateRequest request) {
        return ResponseMessage.ok();
    }

    @PostMapping("/exclude")
    @Override
    public ResponseMessage<TestExcludeBody> testExclude(@RequestParam(value = "exclude", required = false) List<String> excludeFieldList, @RequestBody TestExcludeBody excludeBody) {
        if (excludeFieldList != null && excludeFieldList.size() > 0) {

            String[] excludeFieldArray = new String[excludeFieldList.size()];
            TestExcludeBody newExcludeBody = new TestExcludeBody();
            BeanUtil.copyProperties(excludeBody, newExcludeBody, excludeFieldList.toArray(excludeFieldArray));
            return ResponseMessage.ok(newExcludeBody);
        } else {
            return ResponseMessage.ok(excludeBody);
        }

    }
}
