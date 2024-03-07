package com.zion.learning.dao;

import com.zion.common.basic.ZWDao;
import com.zion.common.vo.resource.request.SubPointQO;
import com.zion.learning.model.SubPoint;

import java.util.List;

public interface SubPointDao extends ZWDao<SubPoint> {

    void saveBatch(List<SubPointQO> subPointList);
}
