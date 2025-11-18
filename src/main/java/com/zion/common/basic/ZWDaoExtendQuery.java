package com.zion.common.basic;

import org.springframework.data.mongodb.core.query.Query;

/**
 * 拓展查询接口
 *
 * @param <Q> 查询参数
 */
public interface ZWDaoExtendQuery<Q> {

    default void appendQuery(Query query, Q q){

    }
}
