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
package com.xenoblade.zohar.framework.commons.api.enums.dynamic;

/**
 * DigitsDynamicEnum
 * @author xenoblade
 * @since 1.0.0
 */
public class DigitsDynamicEnum extends DynamicEnum<DigitsDynamicEnum>{

    public final static DigitsDynamicEnum ZERO = new DigitsDynamicEnum("ZERO", 0);
    public final static DigitsDynamicEnum ONE = new DigitsDynamicEnum("ONE", 1);
    public final static DigitsDynamicEnum TWO = new DigitsDynamicEnum("TWO", 2);
    public final static DigitsDynamicEnum THREE = new DigitsDynamicEnum("THREE", 3);


    protected DigitsDynamicEnum(String name, int ordinal) {
        super(name, ordinal);
    }

    public static <E> DynamicEnum<? extends DynamicEnum<?>>[] values() {
        return values(DigitsDynamicEnum.class);
    }

}
