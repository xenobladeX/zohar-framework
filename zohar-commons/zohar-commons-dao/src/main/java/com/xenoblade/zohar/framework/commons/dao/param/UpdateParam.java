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
package com.xenoblade.zohar.framework.commons.dao.param;

import com.xenoblade.zohar.framework.commons.dao.param.term.Term;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UpdateParam
 * @author xenoblade
 * @since 1.0.0
 */
public class UpdateParam<T> extends Param {
    private T data;

    public UpdateParam() {
    }

    public UpdateParam(T data) {
        this.data = data;
    }

    public <C extends UpdateParam<T>> C set(T data) {
        this.data = data;
        return (C) this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public UpdateParam clone() {
        UpdateParam param = new UpdateParam();
        param.setData(data);
        param.setExcludes(new LinkedHashSet<>(excludes));
        param.setIncludes(new LinkedHashSet<>(includes));
        List<Term> terms = this.terms.stream().map(term -> term.clone()).collect(Collectors.toList());
        param.setTerms(terms);
        return param;
    }
}
