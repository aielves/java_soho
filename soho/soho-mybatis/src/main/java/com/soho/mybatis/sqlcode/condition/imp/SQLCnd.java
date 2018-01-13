package com.soho.mybatis.sqlcode.condition.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.soho.mybatis.pageable.Pagination;
import com.soho.mybatis.pageable.imp.SimplePagination;
import com.soho.mybatis.sqlcode.aconst.OPT;
import com.soho.mybatis.sqlcode.aconst.SortBy;
import com.soho.mybatis.sqlcode.condition.Cnd;
import com.soho.mybatis.sqlcode.domain.Condition;

import java.util.*;

public class SQLCnd implements Cnd {

    private List<Condition<?>> conditions = new ArrayList<Condition<?>>();
    private List<String> fields = new ArrayList<>();
    private List<String> distincts = new ArrayList<String>();
    private List<String> groupbys = new ArrayList<String>();
    private List<Condition<?>> orderbys = new ArrayList<Condition<?>>();
    private Map<String, Object> updateObj = new HashMap<String, Object>();
    private Map<String, Object> other = new HashMap<>();

    private Pagination<Object> pagination;

    @Override
    public Cnd eq(String key, Object value) {
        conditions.add(new Condition<Object>(OPT.EQ, key, value));
        return this;
    }

    @Override
    public Cnd noteq(String key, Object value) {
        conditions.add(new Condition<Object>(OPT.NOT_EQ, key, value));
        return this;
    }

    @Override
    public Cnd lt(String key, Object value) {
        conditions.add(new Condition<Object>(OPT.LT, key, value));
        return this;
    }

    @Override
    public Cnd lte(String key, Object value) {
        conditions.add(new Condition<Object>(OPT.LTE, key, value));
        return this;
    }

    @Override
    public Cnd gt(String key, Object value) {
        conditions.add(new Condition<Object>(OPT.GT, key, value));
        return this;
    }

    @Override
    public Cnd gte(String key, Object value) {
        conditions.add(new Condition<Object>(OPT.GTE, key, value));
        return this;
    }

    @Override
    public Cnd isnull(String key) {
        conditions.add(new Condition<Object>(OPT.IS_NULL, key, ""));
        return this;
    }

    @Override
    public Cnd notnull(String key) {
        conditions.add(new Condition<Object>(OPT.IS_NOT_NULL, key, ""));
        return this;
    }

    @Override
    public Cnd between(String key, Object value1, Object value2) {
        conditions.add(new Condition<Object>(OPT.BETWEEN, key, Arrays.asList(value1, value2)));
        return this;
    }

    @Override
    public Cnd notbetween(String key, Object value1, Object value2) {
        conditions.add(new Condition<Object>(OPT.NOT_BETWEEN, key, Arrays.asList(value1, value2)));
        return this;
    }

    @Override
    public Cnd in(String key, Object... values) {
        if (values.length == 1 && values[0] instanceof List<?>) {
            conditions.add(new Condition<Object>(OPT.IN, key, (List<Object>) values[0]));
        } else {
            conditions.add(new Condition<Object>(OPT.IN, key, Arrays.asList(values)));
        }
        return this;
    }

    @Override
    public Cnd in(String key, Object values) {
        if (values instanceof List<?>) {
            conditions.add(new Condition<Object>(OPT.IN, key, (List<Object>) values));
        }
        return this;
    }

    @Override
    public Cnd notin(String key, Object... values) {
        if (values.length == 1 && values[0] instanceof List<?>) {
            conditions.add(new Condition<Object>(OPT.NOT_IN, key, (List<Object>) values[0]));
        } else {
            conditions.add(new Condition<Object>(OPT.NOT_IN, key, Arrays.asList(values)));
        }
        return this;
    }

    @Override
    public Cnd notin(String key, Object values) {
        if (values instanceof List<?>) {
            conditions.add(new Condition<Object>(OPT.NOT_IN, key, (List<Object>) values));
        }
        return this;
    }

    @Override
    public Cnd like(String key, Object value) {
        conditions.add(new Condition<Object>(OPT.LIKE, key, value));
        return this;
    }

    @Override
    public Cnd notlike(String key, Object value) {
        conditions.add(new Condition<Object>(OPT.NOT_LIKE, key, value));
        return this;
    }

    @Override
    public Cnd or(Cnd... cnds) {
        List<Object> values = new LinkedList<Object>();
        for (Cnd cnd : cnds) {
            values.add(cnd.getConditions());
        }
        conditions.add(new Condition<Object>(OPT.OR, "", values));
        return this;
    }

    @Override
    public Cnd limit(Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 1000) {
            pageSize = 10;
        }
        pagination = new SimplePagination<Object>(pageNo, pageSize);
        return this;
    }

    @Override
    public Cnd distinct(String... keys) {
        for (String object : keys) {
            distincts.add(object);
        }
        return this;
    }

    @Override
    public Cnd groupby(String... keys) {
        for (String object : keys) {
            groupbys.add(object);
        }
        return this;
    }

    @Override
    public Cnd orderby(String key, SortBy sortBy) {
        orderbys.add(new Condition<Object>(OPT.ORDERBY, key, sortBy.toString()));
        return this;
    }

    @Override
    public Cnd fields(String... keys) {
        for (String object : keys) {
            fields.add(object);
        }
        return this;
    }

    @Override
    public Cnd only(String field) {
        fields.clear();
        fields.add(field);
        return this;
    }

    @Override
    public List<Condition<?>> getConditions() {
        return conditions;
    }

    @Override
    public Pagination<Object> getPagination() {
        return pagination;
    }

    @Override
    public void setPagination(Pagination<Object> pagination) {
        this.pagination = pagination;
    }

    @Override
    public Cnd addUpdateObj(Object obj) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);
        Set<String> keySet = jsonObject.keySet();
        if (keySet != null && !keySet.isEmpty()) {
            for (String key : keySet) {
                updateObj.put(key, jsonObject.get(key));
            }
        }
        return this;
    }

    @Override
    public Cnd addUpdateKeyValue(String[] keys, Object[] values) {
        if (keys != null && values != null && keys.length > 0 && keys.length == values.length) {
            for (int i = 0; i < keys.length; i++) {
                updateObj.put(keys[i], values[i]);
            }
        }
        return this;
    }

    @Override
    public Cnd addOther(String key, Object value) {
        other.put(key, value);
        return this;
    }

    public String getOnlyField() {
        return fields.get(0);
    }

    @Override
    public Cnd copy(Cnd cnd) {
        if (cnd instanceof SQLCnd) {
            SQLCnd sqlCnd = (SQLCnd) cnd;
            conditions.addAll(sqlCnd.conditions);
            fields.addAll(sqlCnd.fields);
            distincts.addAll(sqlCnd.distincts);
            groupbys.addAll(sqlCnd.groupbys);
            orderbys.addAll(sqlCnd.orderbys);
            updateObj.putAll(sqlCnd.updateObj);
            other.putAll(sqlCnd.other);
            if (sqlCnd.pagination != null) {
                pagination = new SimplePagination<>();
                pagination.setPageNumber(sqlCnd.pagination.getPageNumber());
                pagination.setPageTotal(sqlCnd.pagination.getPageTotal());
                pagination.setPageSize(sqlCnd.pagination.getPageSize());
                pagination.setPageNo(sqlCnd.pagination.getPageNo());
                pagination.setData(sqlCnd.pagination.getData());
            }
        }
        return this;
    }

}
