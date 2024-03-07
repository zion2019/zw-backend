package com.zion.learning.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.TopicQO;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.learning.model.Topic;

import java.util.List;

public interface TopicService {

    boolean save(TopicQO qo) ;

    List tree(Long currentUserId,Long excludeId,Boolean root);

    TopicVO info(Long id, Long currentUserId);

    boolean delete(Long topicId);

    void increaseWeight(Long topicId) ;

    Page list(TopicQO condition) ;

    /**
     * find simple info for practise
     * @param topics
     * @return
     */
    List<TopicVO> getTitleByIds(List<Long> topics);
}
