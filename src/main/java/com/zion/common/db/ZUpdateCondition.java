package com.zion.common.db;

import com.zion.common.basic.BaseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ZUpdateCondition<M extends BaseEntity> extends ZCondition<M> {
    private final Map<String, Object> set = new LinkedHashMap<>();

    public Map<String, Object> getSet() {
        return set;
    }

    /* *********** 链式 API *********** */
    public <R> ZCondition<M> set(ZFunction<M, R> column, R val) {
        set.put(ZLambdaUtils.getField(column), val);
        return this;
    }



}
