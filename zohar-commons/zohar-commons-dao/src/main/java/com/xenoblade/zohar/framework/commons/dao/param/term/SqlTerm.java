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
package com.xenoblade.zohar.framework.commons.dao.param.term;

/**
 * 直接拼接sql的方式
 * @author xenoblade
 * @since 1.0.0
 */
public class SqlTerm extends Term {

    private String sql;

    private Object param;

    public SqlTerm() {
    }

    public SqlTerm(String sql) {
        this(sql, null);
    }

    public SqlTerm(String sql, Object param) {
        this.sql = sql;
        this.param = param;
        setColumn(sql);
        if (param == null) {
            param = sql;
        }
        setValue(param);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        setColumn(sql);
        this.sql = sql;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        setValue(param);
        this.param = param;
    }

    @Override
    public SqlTerm clone() {
        SqlTerm term = new SqlTerm();
        term.setColumn(getColumn());
        term.setValue(getValue());
        term.setTermType(getTermType());
        term.setType(getType());
        term.setSql(getSql());
        term.setParam(getParam());
        getTerms().forEach(t -> term.addTerm(t.clone()));
        return term;
    }
}
