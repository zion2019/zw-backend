package com.zion.learning.dao;

import com.zion.common.basic.Page;
import com.zion.common.basic.ZWDao;
import com.zion.learning.model.Practice;

import java.util.List;

public interface PracticeDao extends ZWDao<Practice> {
    /**
     * get group topic
     * @param pageNo
     * @param pageSize
     * @param build
     * @return
     */
    Page findUndoTopicByDate(Integer pageNo, Integer pageSize, Practice build);
}
