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
package com.xeonblade.zohar.framework.commons.authorization.api.token;

import com.xenoblade.zohar.framework.commons.api.bean.Bean;

import java.util.Map;

/**
 * 生成好的令牌信息
 * @author xenoblade
 * @since 1.0.0
 */
public interface GeneratedToken extends Bean{

    /**
     * 要响应的数据,可自定义想要的数据给调用者
     *
     * @return {@link Map}
     */
    Map<String, Object> getResponse();

    /**
     * @return 令牌字符串, 令牌具有唯一性, 不可逆, 不包含敏感信息
     */
    String getToken();

    /**
     * @return 令牌类型
     */
    String getType();

    /**
     * @return 令牌有效期（单位毫秒）
     */
    int getTimeout();


}
