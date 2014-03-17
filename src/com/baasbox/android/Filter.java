/*
 * Copyright (C) 2014. BaasBox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.baasbox.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a query filter that can be applied
 * to batch requests on collections, users and files.
 *
 * @author Andrea Tortorella
 * @since 0.7.3
 */
public class Filter {
// ------------------------------ FIELDS ------------------------------

    /**
     * A filter that does not apply any restriction to the request.
     */
    public static final Filter ANY = new Filter() {
        @Override
        RequestFactory.Param[] toParams() {
            return null;
        }
    };

    StringBuilder where = null;
    List<CharSequence> params = null;
    String orderBy;
    BaasQuery.Paging paging;

// -------------------------- STATIC METHODS --------------------------

    /**
     * Returns a new filter that applies pagination to the request using
     * the given order, page number and records page.
     *
     * @param order   the field to use for sorting
     * @param asc     true if sorting should be ascending false otherwise
     * @param page    the page number to retrieve
     * @param records the number of entity to return per page
     * @return a configured filter
     */
    public static Filter paging(String order, boolean asc, int page, int records) {
        Filter f = new Filter();
        f.setOrderBy(order + (asc ? " ASC" : " DESC"));
        f.setPaging(page, records);
        return f;
    }

    private Filter(){}

    Filter(String where,List<CharSequence> params,String sort,BaasQuery.Paging paging){
        this.where=where==null?null:new StringBuilder(where);
        this.params = params==null||params.size()==0?null:new ArrayList<CharSequence>();
        if(this.params!=null)this.params.addAll(params);
        this.orderBy=sort;
        this.paging=paging;
    }

    /**
     * Sets the sort order to use with this filter.
     *
     * @param name
     * @return
     */
    public Filter setOrderBy(String name) {
        this.orderBy = name;
        return this;
    }

    /**
     * Configures pagination for this filter
     *
     * @param page
     * @param numrecords
     * @return
     */
    public Filter setPaging(int page, int numrecords) {
        if (this.paging == null) {
            this.paging = new BaasQuery.Paging(page,numrecords);
        }
        return this;
    }

    /**
     * Returns a new filter that applies the given <code>where</code> condition to the request.
     * Where condition can be parameterized using '?', params will be filled using
     * the provided <code>params</code>.
     * Where conditions are simply passed to the server database,
     * their syntax is thus the same of OrientDB see:
     * <a href="https://github.com/orientechnologies/orientdb/wiki/SQL-Where">Orient SQL Where reference</a>
     * for a complete reference.
     *
     * @param where  a string
     * @param params params to fill in the condition
     * @return a configured filter
     */
    public static Filter where(String where, Object... params) {
        return new Filter().setWhere(where, params);
    }

    /**
     * Sets the where condition for this filter,
     *
     * @param clause a string
     * @param args   arguments to use in the condition
     * @return this filter with this where condition set
     * @see com.baasbox.android.Filter#where(String, Object...)
     */
    public Filter setWhere(CharSequence clause, Object... args) {
        where = null;
        if (clause == null) return this;

        where = new StringBuilder(clause.length() + 16);
        where.append(clause);
        if (args != null) {
            if (params == null) {
                params = new ArrayList<CharSequence>(args.length);
            } else {
                params.clear();
            }
            for (Object a : args) {
                params.add(a==null?"null":a.toString());
            }
        } else {
            if (params != null) {
                params.clear();
            }
        }
        return this;
    }

    /**
     * Returns a new filter that applies the provided sort order to the request.
     *
     * @param order a field to use for sorting
     * @param asc   true if sorting should be ascending false otherwise
     * @return a configured Filter
     */
    public static Filter sort(String order, boolean asc) {
        return new Filter().setOrderBy(order + (asc ? " ASC" : " DESC"));
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Removes the pagination from this filter
     *
     * @return
     */
    public Filter clearPaging() {
        this.paging = null;
        return this;
    }

    private int countParams() {
        int count = 0;
        if (where != null) {
            count += 1;
            if (params != null) {
                count += params.size();
            }
        }
        if (orderBy != null) {
            count += 1;
        }
        if (paging != null) {
            count += 2;
        }
        return count;
    }

    /**
     * Configures pagination for this filter.
     *
     * @param orderBy
     * @param page
     * @param numrecords
     * @return
     */
    public Filter setPaging(String orderBy, int page, int numrecords) {
        this.orderBy = orderBy;
        if (this.paging == null) {
            this.paging = new BaasQuery.Paging(page,numrecords);
        }
        paging.page = page;
        paging.records = numrecords;
        return this;
    }

    RequestFactory.Param[] toParams() {
        validate();
        List<RequestFactory.Param> reqParams = new ArrayList<RequestFactory.Param>();
        if (where != null) {
            reqParams.add(new RequestFactory.Param("where", where.toString()));
            if (params != null) {
                for (CharSequence p : params) {
                    reqParams.add(new RequestFactory.Param("params", p.toString()));
                }
            }
        }
        if (orderBy != null) {
            reqParams.add(new RequestFactory.Param("orderBy", orderBy.toString()));
        }
        if (paging != null) {
            reqParams.add(new RequestFactory.Param("page", Integer.toString(paging.page)));
            reqParams.add(new RequestFactory.Param("recordsPerPage", Integer.toString(paging.records)));
        }
        if (reqParams.size() == 0) return null;
        return reqParams.toArray(new RequestFactory.Param[reqParams.size()]);
    }

    private void validate() {
        if (paging != null) {
            if (orderBy == null) throw new IllegalArgumentException("paging requires order by");
        }
    }

// -------------------------- INNER CLASSES --------------------------

//    private static class Paging {
//        int page;
//        int num;
//    }
}
