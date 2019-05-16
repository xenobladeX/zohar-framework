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
package com.xenoblade.zohar.framework.cache.aspectj.support;

/**
 * 抽象的调用缓存操作方法
 * <p>不提供传输已检查异常的方法，但提供了一个特殊异常，该异常应该用于包装底层调用引发的任何异常。
 * 调用者应特别处理此异常类型。
 * @author xenoblade
 * @since 1.0.0
 */
public interface CacheOperationInvoker {

    /**
     * 调用此实例定义的缓存操作.
     * Wraps any exception that is thrown during the invocation in a {@link ThrowableWrapperException}.
     *
     * @return the result of the operation
     * @throws ThrowableWrapperException if an error occurred while invoking the operation
     */
    Object invoke() throws ThrowableWrapperException;

    /**
     * Wrap any exception thrown while invoking {@link #invoke()}.
     */
    class ThrowableWrapperException extends RuntimeException {

        private final Throwable original;

        public ThrowableWrapperException(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }

}
