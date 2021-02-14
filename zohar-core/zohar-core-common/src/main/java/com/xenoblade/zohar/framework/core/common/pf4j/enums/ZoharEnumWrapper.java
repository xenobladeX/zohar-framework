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
package com.xenoblade.zohar.framework.core.common.pf4j.enums;

import com.xenoblade.zohar.framework.commons.api.enums.IEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Type ZoharEnumWrapper
 * @Desc ZoharEnumWrapper
 * @author dingming
 * @date 2020年12月16日 7:33 下午
 * @version
 */
@Data
@Accessors(chain = true)
public class ZoharEnumWrapper<T extends IEnum> implements Serializable {

    private static final long serialVersionUID = -6596853117193325246L;

    private T enumValue;

    private String pluginId;

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 *
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2020年12月16日 7:33 下午 dingming create
 */
