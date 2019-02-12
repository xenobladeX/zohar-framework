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
package com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * 默认的SESSION监听
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public class DefaultSessionListener implements SessionListener {

    /**
     * 会话开始
     */
    @Override
    public void onStart(Session session) {
        log.info("创建session:("+session.getId()+","+session.getHost()+")");
    }
    /**
     * 会话结束
     */
    @Override
    public void onStop(Session session) {
        log.info("结束session:("+session.getId()+","+session.getHost()+")");
    }
    /**
     * 会话过期
     */
    @Override
    public void onExpiration(Session session) {
        log.info("过期session:("+session.getId()+","+session.getHost()+")");
    }

}
