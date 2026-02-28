package com.zion.common.db;

import cn.hutool.core.lang.Pair;
import com.zion.common.basic.BaseEntity;

import java.util.*;

/**
 * 描述：ZDao 数据库查询条件
 * @author zion
 */
public class ZCondition<M extends BaseEntity> {
    // 等值条件  field -> value
    private final Map<String, Object> eq = new LinkedHashMap<>();
    private final Map<String, Object> like = new LinkedHashMap<>();
    private final Map<String, Object> ge = new LinkedHashMap<>();
    private final Map<String, Object> le = new LinkedHashMap<>();
    private final Map<String, Object> ne = new LinkedHashMap<>();
    private final Map<String, Object> in = new LinkedHashMap<>();
    // 排序  field -> Order
    private final List<Pair<String, ZOrder>> orderBy = new ArrayList<>();
    // 只查这些字段（null 表示全部）
    private String[] includeFields;

    /* *********** 链式 API *********** */
    public <R> ZCondition<M> eq(boolean isEq,ZFunction<M, R> column, R val) {
        if(isEq){
            return eq(column, val);
        }
        return this;
    }
    public <R> ZCondition<M> like(boolean isLike,ZFunction<M, R> column, R val) {
        if(isLike){
            return like(column, val);
        }
        return this;
    }
    public <R> ZCondition<M> in(boolean isIn,ZFunction<M, R> column, R val){
        if(isIn){
            return in(column, val);
        }
        return this;
    }

    public <R> ZCondition<M> eq(ZFunction<M, R> column, R val) {
        String field = ZLambdaUtils.getField(column);
        eq.put(field, val);
        return this;
    }


    public <R> ZCondition<M> in(ZFunction<M, R> column, R val) {
        String field = ZLambdaUtils.getField(column);
        in.put(field, val);
        return this;
    }
    public <R> ZCondition<M> ne(ZFunction<M, R> column, R val) {
        String field = ZLambdaUtils.getField(column);
        ne.put(field, val);
        return this;
    }
    public <R> ZCondition<M> like(ZFunction<M, R> column, R val) {
        String field = ZLambdaUtils.getField(column);
        like.put(field, val);
        return this;
    }
    public <R> ZCondition<M> ge(ZFunction<M, R> column, R val) {
        String field = ZLambdaUtils.getField(column);
        ge.put(field, val);
        return this;
    }
    public <R> ZCondition<M> le(ZFunction<M, R> column, R val) {
        String field = ZLambdaUtils.getField(column);
        le.put(field, val);
        return this;
    }




    public <R> ZCondition<M> order(ZFunction<M, R> column, ZOrder o) {
        String field = ZLambdaUtils.getField(column);
        orderBy.add(Pair.of(field, o));
        return this;
    }
    public ZCondition<M> select(ZFunction<M, ?>... columns) {
        includeFields = Arrays.stream(columns)
                .map(ZLambdaUtils::getField)
                .toArray(String[]::new);
        return this;
    }

    /* *********** get 留给 DAO 用 *********** */
    public Map<String, Object> getEq() { return eq; }
    public Map<String, Object> getLe() { return le; }
    public Map<String, Object> getGe() { return ge; }
    public Map<String, Object> getLike() { return like; }
    public Map<String, Object> getNe() { return ne; }
    public Map<String, Object> getIn() { return in; }
    public List<Pair<String, ZOrder>> getOrderBy() { return orderBy; }
    public String[] getIncludeFields() { return includeFields; }
}
