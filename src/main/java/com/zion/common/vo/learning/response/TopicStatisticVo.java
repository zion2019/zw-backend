package com.zion.common.vo.learning.response;

import lombok.Data;

/**
 * 主题统计信息
 */
@Data
public class TopicStatisticVo {

    /**
     * 主题下知识点数量统计
     */
    private Integer pointCount;

    /**
     *  掌握程度
     */
    private Integer percentOfMastery;
}
