package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.learning.common.constents.MasteryLevel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class KnowledgePointVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long stageId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long subjectId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private Long strategyId;
    private MasteryLevel masteryLevel;

    private Integer breakdownCount;
    private LocalDateTime nextReviewTime;
}