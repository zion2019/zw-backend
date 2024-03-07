package com.zion.common.basic;

import com.zion.common.vo.resource.request.SubPointQO;
import com.zion.learning.model.Practice;

import java.util.List;

public interface ZWDao<M> {
    M getById(Long id);

    long conditionCount(M condition);

    M save(M model);

    List<M> condition(M condition);

    M conditionOne(M condition);

    long delete(M condition);

    Page pageQuery(Page page, Class targetClazz, M condition);

    Long remove(M condition);

}
