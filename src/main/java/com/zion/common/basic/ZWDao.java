package com.zion.common.basic;

import java.util.List;

public interface ZWDao<M> {
    M getById(Long id);

    long conditionCount(M condition);

    M save(M model);

    List<M> condition(M condition);

    M conditionOne(M condition);

    long delete(M condition);

    <T> Page<T> pageQuery(Page<T> page, Class<T> targetClazz, M condition);

    void remove(M condition);

}
