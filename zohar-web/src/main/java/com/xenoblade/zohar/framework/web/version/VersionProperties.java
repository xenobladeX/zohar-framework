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
package com.xenoblade.zohar.framework.web.version;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * VersionProperties
 * @author xenoblade
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "zohar.version")
@Data
public class VersionProperties {

    /**
     * 是否开启版本控制
     */
    private boolean enabled = true;
    /**
     * 最小版本号，小于该版本号返回版本过时。
     */
    private int minimumVersion;

    /**
     * 开启通过包名解析版本号，包名中存在v1、v2时解析。
     */
    private boolean parsePackageVersion = true;

}
