package com.zion.learning.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.learning.response.PointVo;
import com.zion.common.vo.learning.response.TopicStatisticVo;
import com.zion.common.vo.learning.request.PointQO;
import com.zion.learning.common.PractiseResult;

import java.util.Date;


public interface PointService {

    /**
     * 统计主题下学习程度
     * @param topicId
     * @return
     */
    TopicStatisticVo statisticMastery(Long topicId);

    /**
     * list with page
     * @param pointQO
     * @return
     */
    Page<PointVo> list(PointQO pointQO) ;

    /**
     * save or update
     * @param pointQO
     * @return
     */
    boolean save(PointQO pointQO) ;

    PointVo info(Long pointId);

    boolean delete(Long pointId);

    boolean upgrade(PractiseResult result, PointQO qo);

    /**
     * judge the topic id is have point
     * @param topicId
     * @return
     */
    boolean existPointByTopicId(Long topicId);
}
