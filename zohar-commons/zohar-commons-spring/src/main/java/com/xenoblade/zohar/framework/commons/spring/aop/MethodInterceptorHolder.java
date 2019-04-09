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
package com.xenoblade.zohar.framework.commons.spring.aop;

import com.google.common.collect.Lists;
import com.xenoblade.zohar.framework.commons.utils.ThreadLocalUtil;
import org.aopalliance.intercept.MethodInvocation;

import java.util.LinkedList;

/**
 * MethodInterceptorHolder
 * @author xenoblade
 * @since 1.0.0
 */
public class MethodInterceptorHolder {

    public static MethodInterceptorHolder current() {
        MethodInterceptorHolder methodInterceptorHolder = ThreadLocalUtil
                .get(MethodInterceptorHolder.class.getName());
        if (methodInterceptorHolder == null) {
            methodInterceptorHolder = new MethodInterceptorHolder();
            setCurrent(methodInterceptorHolder);
        }
        return methodInterceptorHolder;
    }

    public static MethodInterceptorHolder clear() {
        return ThreadLocalUtil.getAndRemove(MethodInterceptorHolder.class.getName());
    }

    public static MethodInterceptorHolder setCurrent(MethodInterceptorHolder holder) {
        return ThreadLocalUtil.put(MethodInterceptorHolder.class.getName(), holder);
    }


    private LinkedList<MethodInterceptorContext> methodInterceptorContextList = Lists.newLinkedList();

    public void add(MethodInterceptorContext methodInterceptorContext) {
        methodInterceptorContextList.addLast(methodInterceptorContext);
    }


    public void add(MethodInvocation invocation) {
        DefaultMethodInterceptorContext methodInterceptorContext = DefaultMethodInterceptorContext.init(invocation);
        methodInterceptorContextList.addLast(methodInterceptorContext);
    }

    public MethodInterceptorContext poll() {
        return methodInterceptorContextList.removeLast();
    }

    public MethodInterceptorContext peek() {
        return methodInterceptorContextList.isEmpty() ? null : methodInterceptorContextList.getLast();
    }

}
