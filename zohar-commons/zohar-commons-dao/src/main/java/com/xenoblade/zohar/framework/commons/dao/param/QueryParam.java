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

import com.google.common.collect.Lists;
import com.xenoblade.zohar.framework.commons.dao.param.column.Sort;
import com.xenoblade.zohar.framework.commons.dao.param.term.Term;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询参数
 * @author xenoblade
 * @since 1.0.0
 */
public class QueryParam  extends Param implements Serializable, Cloneable{
    private static final long serialVersionUID = -6862687936910572543L;

    /**
     * 是否进行分页，默认为true
     */
    private boolean paging = true;

    /**
     * 第几页 从0开始
     */
    private int pageIndex = 0;

    /**
     * 每页显示记录条数
     */
    private int pageSize = 25;


    /**
     * 排序字段
     *
     * @since 1.0
     */
    private List<Sort> sorts = Lists.newLinkedList();

    private boolean forUpdate = false;

    public Sort orderBy(String column) {
        Sort sort = new Sort(column);
        sorts.add(sort);
        return sort;
    }

    public <Q extends QueryParam> Q doPaging(int pageIndex) {
        this.pageIndex = pageIndex;
        this.paging = true;
        return (Q) this;
    }

    public <Q extends QueryParam> Q doPaging(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.paging = true;
        return (Q) this;
    }

    public <Q extends QueryParam> Q rePaging(int total) {
        paging = true;
        // 当前页没有数据后跳转到最后一页
        if (this.getPageIndex() != 0 && (pageIndex * pageSize) >= total) {
            int tmp = total / this.getPageSize();
            pageIndex = total % this.getPageSize() == 0 ? tmp - 1 : tmp;
        }
        return (Q) this;
    }

    public boolean isPaging() {
        return paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public void setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public boolean isForUpdate() {
        return forUpdate;
    }

    @Override
    public QueryParam clone() {
        QueryParam sqlParam = new QueryParam();
        sqlParam.setExcludes(new LinkedHashSet<>(excludes));
        sqlParam.setIncludes(new LinkedHashSet<>(includes));
        List<Term> terms = this.terms.stream().map(Term::clone).collect(Collectors.toList());
        sqlParam.setTerms(terms);
        sqlParam.setPageIndex(pageIndex);
        sqlParam.setPageSize(pageSize);
        sqlParam.setPaging(paging);
        sqlParam.setSorts(sorts);
        sqlParam.setForUpdate(forUpdate);
        return sqlParam;
    }

}
