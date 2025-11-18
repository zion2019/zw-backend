package com.zion.common.vo.learning.response;

import com.zion.learning.common.constents.MasteryLevel;
import lombok.Data;

import java.io.Serializable;

@Data
public class MasteryBubbleVO implements Serializable {
    /**
     * 知识点ID
     */
    private Long knowledgePointId;
    
    /**
     * 知识点名称
     */
    private String knowledgePointName;
    
    /**
     * 掌握程度
     */
    private MasteryLevel masteryLevel;
    
    /**
     * 最后复习时间
     */
    private String lastReviewTime;
}