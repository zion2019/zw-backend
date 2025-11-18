package com.zion.common.vo.learning.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StatisticsSummaryVO implements Serializable {
    /**
     * 知识点总数
     */
    private Integer totalKnowledgePoints;
    
    /**
     * 已精通知识点数
     */
    private Integer masteredKnowledgePoints;
    
    /**
     * 复习总次数
     */
    private Integer totalReviewCount;
    
    /**
     * 累计学习时长(小时)
     */
    private BigDecimal totalStudyHours;
}