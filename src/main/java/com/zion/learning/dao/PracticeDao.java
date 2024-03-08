package com.zion.learning.dao;

import com.zion.common.basic.Page;
import com.zion.common.basic.ZWDao;
import com.zion.common.vo.learning.response.PractiseVO;
import com.zion.learning.model.Practice;

public interface PracticeDao extends ZWDao<Practice> {
    /**
     * get group topic
     */
    Page<PractiseVO> findUndoTopicByDate(Integer pageNo, Integer pageSize, Practice build);
}
