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
package com.xenoblade.zohar.sample.agares.service.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.xenoblade.zohar.framework.commons.api.EErrorCode;
import com.xenoblade.zohar.framework.commons.spring.log.api.AccessLogger;
import com.xenoblade.zohar.sample.agares.api.Agares.AgaresService;
import com.xenoblade.zohar.sample.agares.api.Agares.HelloAgaresRequest;
import com.xenoblade.zohar.sample.agares.api.Agares.HelloAgaresResponse;
import com.xenoblade.zohar.sample.agares.api.AgaresService;

/**
 * DefaultAgaresService
 * @author xenoblade
 * @since 1.0.0
 */
@Service
public class DefaultAgaresService implements AgaresService.BlockingInterface {

    @Override public HelloAgaresResponse helloAgares(RpcController controller,
                                                     HelloAgaresRequest request) {
        return HelloAgaresResponse.newBuilder().setResponse("response from Agares")
                .setErrorcode(EErrorCode.OK.getCode()).build();
    }
}
