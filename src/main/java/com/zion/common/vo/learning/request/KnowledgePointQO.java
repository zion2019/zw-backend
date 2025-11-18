package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import com.zion.learning.common.constents.MasteryLevel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgePointQO extends Page {
    
    private Long id;
    private String title;
    private Long stageId;
    private Long subjectId;
    private Long userId;
    private Long strategyId;
    private Integer breakdownCount;
    private MasteryLevel masteryLevel;
    private LocalDateTime nextReviewTime;
}