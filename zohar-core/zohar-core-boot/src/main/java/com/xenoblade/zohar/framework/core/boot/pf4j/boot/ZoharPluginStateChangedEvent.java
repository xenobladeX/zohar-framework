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
package com.xenoblade.zohar.framework.core.boot.pf4j.boot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * This event will be published to <b>main app application context</b> when any plugin is changed in batch.
 * Plugins' state might be manipulate in batch, like start up with main app/restart all, etc.
 * This event is useful if you need to do something after all plugin manipulation is done.
 * <br>
 * For example. When plugin jar file get updated, the previous register classloader will not be able to
 * access its resource file anymore. For batch plugin jar files updating, refreshing stuffs could only be
 * done after all plugins reloaded and new plugin classloaders provided.
 * @author xenoblade
 * @since 1.0.0
 */
public class ZoharPluginStateChangedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 5728257161363653229L;

    public ZoharPluginStateChangedEvent(ApplicationContext mainApplicationContext) {
        super(mainApplicationContext);
    }
}
