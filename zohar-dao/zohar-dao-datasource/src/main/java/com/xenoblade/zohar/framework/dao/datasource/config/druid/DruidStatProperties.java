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
package com.xenoblade.zohar.framework.dao.datasource.config.druid;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * DruidStatProperties
 * @author xenoblade
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class DruidStatProperties implements Serializable {

    private static final long serialVersionUID = -1494619645056821902L;

    private String[] aopPatterns;

    private WebStatFilter webStatFilter = new WebStatFilter();

    private StatViewServlet statViewServlet = new StatViewServlet();


    @Data
    @Accessors(chain = true)
    public static class WebStatFilter implements Serializable{

        private static final long serialVersionUID = -3487048122964069665L;

        private boolean enabled = true;
        private String urlPattern;
        private String exclusions;
        private String sessionStatMaxCount;
        private String sessionStatEnable;
        private String principalSessionName;
        private String principalCookieName;
        private String profileEnable;
    }


    @Data
    @Accessors(chain = true)
    public static class StatViewServlet implements Serializable{

        private static final long serialVersionUID = -8900188751812902321L;

        private boolean enabled = true;
        private String urlPattern;
        private String allow;
        private String deny;
        private String loginUsername;
        private String loginPassword;
        private String resetEnable;


    }


}
